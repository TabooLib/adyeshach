package ink.ptms.adyeshach.impl.nms

import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.api.AdyeshachEntityTypeHandler
import ink.ptms.adyeshach.common.api.MinecraftHelper
import ink.ptms.adyeshach.common.bukkit.BukkitPaintings
import ink.ptms.adyeshach.common.bukkit.BukkitParticles
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.util.errorBy
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.TropicalFish
import org.bukkit.material.MaterialData
import org.bukkit.util.Vector
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.invokeMethod
import taboolib.module.nms.MinecraftVersion

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.DefaultMinecraftUtils
 *
 * @author 坏黑
 * @since 2022/6/28 00:07
 */
class DefaultMinecraftHelper : MinecraftHelper {

    val majorLegacy = MinecraftVersion.majorLegacy

    val typeHandler: AdyeshachEntityTypeHandler
        get() = Adyeshach.api().getEntityTypeHandler()

    val nms13ParticleRegistryBlocks: NMS13IRegistry<NMS13Particle<out NMS13ParticleParam>>
        get() = NMS13IRegistry::class.java.getProperty("PARTICLE_TYPE", isStatic = true)!!

    override fun adapt(type: EntityTypes): Any {
        return if (majorLegacy >= 11400) {
            val names = ArrayList<String>()
            names += type.name
            names += typeHandler.getBukkitEntityAliases(type)
            names.forEach { kotlin.runCatching { return NMS16EntityTypes::class.java.getProperty<Any>(it, isStatic = true)!! } }
            errorBy("error-entity-type-not-supported", "$type $names")
        } else {
            typeHandler.getBukkitEntityId(type)
        }
    }

    override fun adapt(location: Location): Any {
        return NMSBlockPosition(location.blockX, location.blockY, location.blockZ)
    }

    override fun adapt(paintings: BukkitPaintings): Any {
        return if (MinecraftVersion.major >= 5) {
            NMS16Paintings::class.java.getProperty<Any>(paintings.index.toString(), isStatic = true)!!
        } else {
            paintings.legacy!!
        }
    }

    @Suppress("KotlinConstantConditions")
    override fun adapt(particles: BukkitParticles): Any {
        return when {
            majorLegacy >= 11400 -> {
                NMS16Particles::class.java.getProperty<Any>(particles.name, isStatic = true) ?: NMS16Particles.FLAME
            }
            majorLegacy >= 11300 -> {
                val particle = nms13ParticleRegistryBlocks.invokeMethod("get", NMS13MinecraftKey(particles.name.lowercase())) ?: NMS13Particles.y
                if (particle is NMS13Particle<*>) {
                    particle.f()
                } else {
                    particle
                }
            }
            else -> 0
        }
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
            (entity as CraftEntity19).handle.getProperty<Any>("entityData")!!
        } else {
            (entity as CraftEntity16).handle.dataWatcher
        }
    }

    override fun getBlockId(materialData: MaterialData): Int {
        return if (MinecraftVersion.major >= 10) {
            NMSBlock::class.java.invokeMethod("getId", CraftMagicNumbers19.getBlock(materialData), isStatic = true)!!
        } else if (MinecraftVersion.major >= 5) {
            NMS16Block.getCombinedId(CraftMagicNumbers16.getBlock(materialData))
        } else {
            materialData.itemType.id + (materialData.data.toInt() shl 12)
        }
    }

    override fun vec3dToVector(vec3d: Any): Vector {
        return Vector((vec3d as NMS16Vec3D).x, vec3d.y, vec3d.z)
    }
}