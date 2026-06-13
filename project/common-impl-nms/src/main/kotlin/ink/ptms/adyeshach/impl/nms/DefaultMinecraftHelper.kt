package ink.ptms.adyeshach.impl.nms

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachEntityTypeRegistry
import ink.ptms.adyeshach.core.MinecraftHelper
import ink.ptms.adyeshach.core.bukkit.BukkitPaintings
import ink.ptms.adyeshach.core.bukkit.BukkitParticles
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.impl.nms.specific.NMS19p
import ink.ptms.adyeshach.impl.nms.specific.NMS20p
import ink.ptms.adyeshach.impl.nms.specific.NMS21
import ink.ptms.adyeshach.minecraft.ChunkPos
import net.minecraft.network.chat.IChatBaseComponent
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.TropicalFish
import org.bukkit.material.MaterialData
import org.bukkit.util.Vector
import taboolib.common.platform.function.warning
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.invokeMethod
import taboolib.library.reflex.Reflex.Companion.unsafeInstance
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsClass
import taboolib.module.nms.versionAdaptor
import taboolib.module.nms.versionStrategy
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.DefaultMinecraftUtils
 *
 * @author 坏黑
 * @since 2022/6/28 00:07
 */
class DefaultMinecraftHelper : MinecraftHelper {

    val majorLegacy = MinecraftVersion.majorLegacy

    val typeHandler: AdyeshachEntityTypeRegistry
        get() = Adyeshach.api().getEntityTypeRegistry()

    val nms13ParticleRegistryBlocks: NMS13IRegistry<NMS13Particle<out NMS13ParticleParam>>
        get() = NMS13IRegistry::class.java.getProperty("PARTICLE_TYPE", isStatic = true)!!

    val entityTypeCache = ConcurrentHashMap<EntityTypes, Any>()

    val paintingCache = ConcurrentHashMap<BukkitPaintings, Any>()

    val particleCache = ConcurrentHashMap<BukkitParticles, Any>()

    val blockIdCache = ConcurrentHashMap<MaterialData, Int>()

    val chunkCheckDisabledUntil = AtomicLong(0)

    override fun adapt(type: EntityTypes): Any {
        return entityTypeCache.getOrPut(type) {
            if (majorLegacy >= 11400) {
                val names = ArrayList<String>()
                names += type.name
                names += typeHandler.getBukkitEntityAliases(type)
                names.forEach { kotlin.runCatching { return@getOrPut NMS16EntityTypes::class.java.getProperty<Any>(it, isStatic = true)!! } }
                errorBy("error-entity-type-not-supported", "$type $names")
            } else {
                typeHandler.getBukkitEntityId(type)
            }
        }!!
    }

    override fun adapt(location: Location): Any {
        return NMSBlockPosition(location.blockX, location.blockY, location.blockZ)
    }

    override fun adapt(paintings: BukkitPaintings): Any {
        return paintingCache.getOrPut(paintings) {
            if (MinecraftVersion.major >= 5) {
                NMS16Paintings::class.java.getProperty<Any>(paintings.index.toString(), isStatic = true)!!
            } else {
                paintings.legacy!!
            }
        }!!
    }

    @Suppress("KotlinConstantConditions")
    override fun adapt(particles: BukkitParticles): Any {
        return particleCache.getOrPut(particles) {
            when {
                majorLegacy >= 11400 -> {
                    NMS16Particles::class.java.getProperty<Any>(particles.name, isStatic = true) ?: NMS16Particles.FLAME
                }

                majorLegacy >= 11300 -> {
                    val particle = nms13ParticleRegistryBlocks.get(NMS13MinecraftKey(particles.name.lowercase()))
                    if (particle is NMS13Particle<*>) {
                        particle.f()
                    } else {
                        particle
                    }
                }

                else -> 0
            }
        }!!
    }

    override fun adaptTropicalFishPattern(data: Int): TropicalFish.Pattern {
        return CraftTropicalFishPattern19.fromData(data and '\uffff'.code)
    }

    override fun adaptTropicalFishPattern(pattern: TropicalFish.Pattern): Int {
        return CraftTropicalFishPattern19.values()[pattern.ordinal].dataValue
    }

    override fun getEntity(world: World, id: Int): Entity? {
        return (world as CraftWorld16).handle.getEntity(id)?.bukkitEntity
    }

    override fun getEntityDataWatcher(entity: Entity): Any {
        // 1.19 dataWatcher -> entityData
        return if (majorLegacy >= 11900) {
            (entity as CraftEntity19).handle.entityData
        } else {
            (entity as CraftEntity16).handle.dataWatcher
        }
    }

    override fun getBlockId(materialData: MaterialData): Int {
        return blockIdCache.getOrPut(materialData) {
            if (MinecraftVersion.major >= 10) {
                NMSBlock.getId(CraftMagicNumbers19.getBlock(materialData))
            } else if (MinecraftVersion.major >= 5) {
                NMS16Block.getCombinedId(CraftMagicNumbers16.getBlock(materialData))
            } else {
                materialData.itemType.id + (materialData.data.toInt() shl 12)
            }
        }!!
    }

    override fun vec3dToVector(vec3d: Any): Vector {
        return Vector((vec3d as NMS16Vec3D).x, vec3d.y, vec3d.z)
    }

    override fun craftChatSerializerToJson(compound: Any): String {
        return if (MinecraftVersion.isUniversal) {
            org.bukkit.craftbukkit.v1_21_R7.util.CraftChatMessage.toJSON(compound as IChatBaseComponent)
        } else {
            NMS16ChatSerializer.a(compound as NMS16IChatBaseComponent)
        }
    }

    override fun literalChatBaseComponent(message: String): Any {
        return CraftChatMessage16.fromString(message)[0]
    }

    // 版本适配：isChunkVisible 的实现策略
    // 首次调用时通过 try-catch 确定可用的 NMS API，后续直接调用缓存的实现
    val chunkVisibleImpl = versionAdaptor<(Player, Int, Int) -> Boolean>(
        versionStrategy("nms21-tracking-view", guard = { MinecraftVersion.versionId >= 12005 }) {
            nmsClass("EntityPlayer").unsafeInstance().invokeMethod<Any>("getChunkTrackingView")
            return@versionStrategy { player: Player, chunkX: Int, chunkZ: Int -> NMS21.instance.isChunkSent(player, chunkX, chunkZ) }
        },
        // 你改你妈个🥚，我爱说实话
        versionStrategy("nms20-player-chunk-loader", guard = { MinecraftVersion.versionId >= 12002 }) {
            nmsClass("WorldServer").unsafeInstance().getProperty<Any>("playerChunkLoader")
            return@versionStrategy { player: Player, chunkX: Int, chunkZ: Int -> NMS20p.instance.isChunkSent(player, chunkX, chunkZ) }
        },
        // 你改你妈个🥚，我爱说实话
        versionStrategy("nms19-chunk-map") {
            CraftWorld19::class.java
            nmsClass("WorldServer").unsafeInstance().invokeMethod<Any>("getChunkSource")
            return@versionStrategy { player: Player, chunkX: Int, chunkZ: Int ->
                val craftWorld = player.world as CraftWorld19
                NMS19p.instance.isChunkSent(player, craftWorld.handle.chunkSource.chunkMap, chunkX, chunkZ)
            }
        },
        // 从 1.18 开始 getVisibleChunk -> getVisibleChunkIfPresent，getChunkProvider -> getChunkSource。
        versionStrategy("legacy-visible-chunk-map-1_18", guard = { MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_18) }) {
            return@versionStrategy { player: Player, chunkX: Int, chunkZ: Int ->
                val craftWorld = player.world as CraftWorld19
                craftWorld.handle.chunkSource.chunkMap.visibleChunkMap.get(ChunkPos.asLong(chunkX, chunkZ)) != null
            }
        },
        // 从 1.14 开始，PlayerChunkMap 改版。
        versionStrategy("legacy-visible-chunk-map-1_14", guard = { MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_14) }) {
            return@versionStrategy { player: Player, chunkX: Int, chunkZ: Int ->
                val craftWorld = player.world as CraftWorld16
                craftWorld.handle.chunkProvider.playerChunkMap.getVisibleChunk(ChunkPos.asLong(chunkX, chunkZ)) != null
            }
        },
        versionStrategy("legacy-player-chunk-map") {
            return@versionStrategy { player: Player, chunkX: Int, chunkZ: Int ->
                val craftWorld = player.world as CraftWorld12
                craftWorld.handle.playerChunkMap.isChunkInUse(chunkX, chunkZ)
            }
        }
    )

    override fun getChunkVisibleStrategy(): String {
        return chunkVisibleImpl.selectedName
    }

    override fun isChunkVisible(player: Player, chunkX: Int, chunkZ: Int): Boolean {
        val now = System.currentTimeMillis()
        if (now < chunkCheckDisabledUntil.get()) {
            return true
        }
        return try {
            chunkVisibleImpl()(player, chunkX, chunkZ)
        } catch (ex: Throwable) {
            val disabledUntil = now + 60000
            if (chunkCheckDisabledUntil.getAndSet(disabledUntil) <= now) {
                warning("Unable to check chunk visibility. Disabled this check for 60 seconds.")
                ex.printStackTrace()
            }
            // 区块发送状态偶发读失败时只短暂熔断，熔断期间退回到调用方已有的距离判断。
            true
        }
    }

    override fun toMinecraft(entity: Entity): Any {
        entity as CraftEntity9
        return entity.handle
    }
}
