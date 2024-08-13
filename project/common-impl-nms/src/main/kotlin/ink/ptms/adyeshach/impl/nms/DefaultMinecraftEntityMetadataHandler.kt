package ink.ptms.adyeshach.impl.nms

import ink.ptms.adyeshach.api.dataserializer.createDataSerializer
import ink.ptms.adyeshach.core.*
import ink.ptms.adyeshach.core.bukkit.BukkitParticles
import ink.ptms.adyeshach.core.bukkit.BukkitPose
import ink.ptms.adyeshach.core.bukkit.data.EmptyVector
import ink.ptms.adyeshach.core.bukkit.data.VillagerData
import ink.ptms.adyeshach.core.entity.type.AdyEntity
import ink.ptms.adyeshach.core.entity.type.AdySniffer
import ink.ptms.adyeshach.impl.entity.DefaultEntityInstance
import ink.ptms.adyeshach.impl.nms.parser.*
import ink.ptms.adyeshach.impl.nmsj17.NMSJ17
import org.bukkit.Art
import org.bukkit.entity.Cat
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import org.bukkit.util.EulerAngle
import org.bukkit.util.Vector
import taboolib.common.platform.function.warning
import taboolib.common5.Quat
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.MinecraftVersion.isUniversal
import java.util.*
import java.util.function.Consumer

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.DefaultMinecraftEntityMetadataHandler
 *
 * @author 坏黑
 * @since 2022/6/28 00:09
 */
class DefaultMinecraftEntityMetadataHandler : MinecraftEntityMetadataHandler {

    val majorLegacy = MinecraftVersion.majorLegacy
    val registeredParser = LinkedHashMap<String, MinecraftMetadataParser<*>>()

    val helper: MinecraftHelper
        get() = Adyeshach.api().getMinecraftAPI().getHelper()

    init {
        addParser("Int", IntParser())
        addParser("Byte", ByteParser())
        addParser("Float", FloatParser())
        addParser("Boolean", BooleanParser())
        addParser("UUID", UUIDParser())
        addParser("String", StringParser())
        addParser("Chat", ChatParser())
        addParser("OptChat", OptChatParser())
        addParser("BlockID", BlockStateParser())
        addParser("OptBlockID", OptBlockStateParser())
        addParser("OptBlockPos", OptBlockPosParser())
        addParser("ItemStack", ItemStackParser())
        addParser("EulerAngle", EulerAngleParser())
        addParser("VillagerData", VillagerDataParser())
        addParser("Particle", BukkitParticleParser())
        addParser("BukkitPose", BukkitPoseParser())
        // 1.19+
        if (MinecraftVersion.majorLegacy >= 11900) {
            addParser("Cat.Type", CatVariantParser())
            addParser("Frog.Variant", FrogVariantParser())
            addParser("Art", PaintingVariantParser())
        }
        // 1.19.4+
        if (MinecraftVersion.majorLegacy >= 11904) {
            addParser("Vector3", Vector3Parser())
            addParser("Quaternion", QuaternionParser())
        }
        // 1.20+
        if (MinecraftVersion.majorLegacy >= 12000) {
            addParser("SnifferState", SnifferStateParser())
        }
        // 注册一个事件专门用来处理 generateMetadata 方法中的特殊实体
        @Suppress("UNCHECKED_CAST")
        Adyeshach.api().getEventBus().prepareMetaUpdate { e ->
            if (e.entity.hasTag("META_GENERATOR")) {
                val record = e.entity.getTag("META_GENERATOR_RECORD") as ArrayList<String>
                record += e.key
            }
            true
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : AdyEntity> buildMetadata(type: Class<T>, process: Consumer<T>): List<MinecraftMeta> {
        val entityType = Adyeshach.api().getEntityTypeRegistry().getEntityTypeFromAdyClass(type) ?: error("Unsupported entity type: $type")
        val entityInstance = Adyeshach.api().getEntityTypeRegistry().getEntityInstance(entityType) as DefaultEntityInstance
        val record = arrayListOf<String>()
        entityInstance.setTag("META_GENERATOR", 1)
        entityInstance.setTag("META_GENERATOR_RECORD", record)
        process.accept(entityInstance as T)
        val generated = arrayListOf<MinecraftMeta>()
        entityInstance.getAvailableEntityMeta().forEach { meta ->
            if (meta.key in record) {
                generated += meta.generateMetadata(entityInstance)
            }
        }
        return generated
    }

    override fun createMetadataPacket(entityId: Int, metaList: List<MinecraftMeta>): Any {
        // 1.19.3 变更为 record 类型，因此无法兼容之前的写法
        return if (majorLegacy >= 11903) {
            NMSJ17.instance.createPacketPlayOutEntityMetadata(entityId, metaList)
        } else if (isUniversal) {
            NMSPacketPlayOutEntityMetadata(createDataSerializer {
                writeVarInt(entityId)
                writeMetadata(metaList)
            }.toNMS() as NMSPacketDataSerializer)
        } else {
            NMS16PacketPlayOutEntityMetadata().also {
                it.a(createDataSerializer {
                    writeVarInt(entityId)
                    writeMetadata(metaList)
                }.toNMS() as NMS16PacketDataSerializer)
            }
        }
    }

    override fun addParser(id: String, metadataParser: MinecraftMetadataParser<*>) {
        registeredParser[id] = metadataParser
    }

    override fun getParser(id: String): MinecraftMetadataParser<*>? {
        return registeredParser[id]
    }

    override fun getParsers(): List<MinecraftMetadataParser<*>> {
        return registeredParser.values.toList()
    }

    override fun createByteMeta(index: Int, value: Byte): MinecraftMeta {
        return DefaultMeta(
            if (majorLegacy >= 11900) {
                NMSDataWatcherItem(NMSDataWatcherObject(index, NMSDataWatcherRegistry.BYTE), value)
            } else {
                NMS16DataWatcherItem(NMS16DataWatcherObject(index, NMS16DataWatcherRegistry.a), value)
            }
        )
    }

    override fun createIntMeta(index: Int, value: Int): MinecraftMeta {
        return DefaultMeta(
            if (majorLegacy >= 11900) {
                NMSDataWatcherItem(NMSDataWatcherObject(index, NMSDataWatcherRegistry.INT), value)
            } else {
                NMS16DataWatcherItem(NMS16DataWatcherObject(index, NMS16DataWatcherRegistry.b), value)
            }
        )
    }

    override fun createFloatMeta(index: Int, value: Float): MinecraftMeta {
        return DefaultMeta(
            if (majorLegacy >= 11900) {
                NMSDataWatcherItem(NMSDataWatcherObject(index, NMSDataWatcherRegistry.FLOAT), value)
            } else {
                NMS16DataWatcherItem(NMS16DataWatcherObject(index, NMS16DataWatcherRegistry.c), value)
            }
        )
    }

    override fun createStringMeta(index: Int, value: String): MinecraftMeta {
        return DefaultMeta(
            if (majorLegacy >= 11900) {
                NMSDataWatcherItem(NMSDataWatcherObject(index, NMSDataWatcherRegistry.STRING), value)
            } else {
                NMS16DataWatcherItem(NMS16DataWatcherObject(index, NMS16DataWatcherRegistry.d), value)
            }
        )
    }

    override fun createChatMeta(index: Int, rawMessage: String): MinecraftMeta {
        return DefaultMeta(
            when {
                majorLegacy >= 11900 -> {
                    NMSDataWatcherItem(
                        NMSDataWatcherObject(index, NMSDataWatcherRegistry.COMPONENT),
                        craftChatMessageFromString(rawMessage) as NMSIChatBaseComponent
                    )
                }
                // 因只在 1.19.4 有应用, 因此不做低版本兼容
                else -> error("Unsupported version.")
            }
        )
    }

    override fun createOptChatMeta(index: Int, rawMessage: String?): MinecraftMeta {
        return DefaultMeta(
            when {
                majorLegacy >= 11900 -> {
                    NMSDataWatcherItem(
                        NMSDataWatcherObject(index, NMSDataWatcherRegistry.OPTIONAL_COMPONENT),
                        Optional.ofNullable(if (rawMessage == null) null else craftChatMessageFromString(rawMessage) as NMSIChatBaseComponent)
                    )
                }

                majorLegacy >= 11300 -> {
                    NMS16DataWatcherItem(
                        NMS16DataWatcherObject(index, NMS16DataWatcherRegistry.f),
                        Optional.ofNullable(if (rawMessage == null) null else craftChatMessageFromString(rawMessage) as NMS16IChatBaseComponent)
                    )
                }

                else -> {
                    NMS12DataWatcherItem(NMS12DataWatcherObject(index, NMS12DataWatcherRegistry.d), rawMessage ?: "")
                }
            }
        )
    }

    override fun createItemStackMeta(index: Int, itemStack: ItemStack): MinecraftMeta {
        return DefaultMeta(
            when {
                majorLegacy >= 11900 -> {
                    NMSDataWatcherItem(
                        NMSDataWatcherObject(index, NMSDataWatcherRegistry.ITEM_STACK),
                        CraftItemStack19.asNMSCopy(itemStack)
                    )
                }

                majorLegacy >= 11300 -> {
                    NMS16DataWatcherItem(
                        NMS16DataWatcherObject(index, NMS16DataWatcherRegistry.g),
                        CraftItemStack16.asNMSCopy(itemStack)
                    )
                }

                majorLegacy >= 11200 -> {
                    NMS12DataWatcherItem(
                        NMS12DataWatcherObject(index, NMS12DataWatcherRegistry.f),
                        CraftItemStack12.asNMSCopy(itemStack)
                    )
                }

                else -> {
                    NMS9DataWatcherItem(
                        NMS9DataWatcherObject(index, NMS9DataWatcherRegistry.f),
                        com.google.common.base.Optional.fromNullable(CraftItemStack9.asNMSCopy(itemStack))
                    )
                }
            }
        )
    }

    override fun createBlockStateMeta(index: Int, blockData: MaterialData): MinecraftMeta {
        return DefaultMeta(
            when {
                majorLegacy >= 11904 -> NMSJ17.instance.createBlockStateMeta(index, blockData)
                else -> error("Unsupported version: $majorLegacy")
            }
        )
    }

    override fun createOptBlockStateMeta(index: Int, blockData: MaterialData?): MinecraftMeta {
        return DefaultMeta(
            when {
                // 在 1.19.4 版本中，BLOCK_STATE 表示 IBlockData ———— 由 OPTIONAL_BLOCK_STATE 代替 Optional<IBlockData>
                majorLegacy >= 11904 -> NMSJ17.instance.createOptBlockStateMeta(index, blockData)
                // 在 1.19.3 版本中，BLOCK_STATE 表示 Optional<IBlockData>
                majorLegacy >= 11900 -> {
                    NMSDataWatcherItem(
                        NMSDataWatcherObject(index, NMSDataWatcherRegistry.BLOCK_STATE),
                        Optional.ofNullable(if (blockData == null) null else CraftMagicNumbers19.getBlock(blockData))
                    )
                }

                majorLegacy >= 11300 -> {
                    NMS13DataWatcherItem(
                        NMS13DataWatcherObject(index, NMS13DataWatcherRegistry.h),
                        Optional.ofNullable(if (blockData == null) null else CraftMagicNumbers13.getBlock(blockData))
                    )
                }

                else -> {
                    NMS12DataWatcherItem(
                        NMS12DataWatcherObject(index, NMS12DataWatcherRegistry.g),
                        com.google.common.base.Optional.fromNullable(
                            if (blockData != null) {
                                CraftMagicNumbers12.getBlock(blockData.itemType).fromLegacyData(blockData.data.toInt())
                            } else {
                                null
                            }
                        )
                    )
                }
            }
        )
    }

    override fun createBooleanMeta(index: Int, value: Boolean): MinecraftMeta {
        return DefaultMeta(
            when {
                majorLegacy >= 11900 -> {
                    NMSDataWatcherItem(NMSDataWatcherObject(index, NMSDataWatcherRegistry.BOOLEAN), value)
                }

                majorLegacy >= 11300 -> {
                    NMS13DataWatcherItem(NMS13DataWatcherObject(index, NMS13DataWatcherRegistry.i), value)
                }

                else -> {
                    NMS11DataWatcherItem(NMS11DataWatcherObject(index, NMS11DataWatcherRegistry.h), value)
                }
            }
        )
    }

    override fun createParticleMeta(index: Int, particle: BukkitParticles): MinecraftMeta {
        val param = helper.adapt(particle)
        return try {
            DefaultMeta(
                when {
                    majorLegacy >= 11900 -> NMSDataWatcherItem(NMSDataWatcherObject(index, NMSDataWatcherRegistry.PARTICLE), param as NMSParticleParam)
                    else -> NMS16DataWatcherItem(NMS16DataWatcherObject(index, NMS16DataWatcherRegistry.j), param as NMS16ParticleParam)
                }
            )
        } catch (ex: ClassCastException) {
            if (particle == BukkitParticles.HAPPY_VILLAGER) {
                error("Particle \"HAPPY_VILLAGER\" is not supported in this version")
            }
            warning("Particle \"$particle\" is not supported in this version")
            return createParticleMeta(index, BukkitParticles.HAPPY_VILLAGER)
        }
    }

    override fun createOptBlockPosMeta(index: Int, vector: Vector?): MinecraftMeta {
        return DefaultMeta(
            when {
                majorLegacy >= 11900 -> {
                    NMSDataWatcherItem(
                        NMSDataWatcherObject(index, NMSDataWatcherRegistry.OPTIONAL_BLOCK_POS),
                        Optional.ofNullable(if (vector == null || vector is EmptyVector) null else NMSBlockPosition(vector.x, vector.y, vector.z))
                    )
                }

                majorLegacy >= 11300 -> {
                    NMS13DataWatcherItem(
                        NMS13DataWatcherObject(index, NMS13DataWatcherRegistry.m),
                        Optional.ofNullable(if (vector == null || vector is EmptyVector) null else NMS13BlockPosition(vector.x, vector.y, vector.z))
                    )
                }

                else -> {
                    NMS12DataWatcherItem(
                        NMS12DataWatcherObject(index, NMS12DataWatcherRegistry.k),
                        com.google.common.base.Optional.fromNullable(
                            if (vector == null || vector is EmptyVector) null else NMS12BlockPosition(vector.x, vector.y, vector.z)
                        )
                    )
                }
            }
        )
    }

    override fun createEulerAngleMeta(index: Int, value: EulerAngle): MinecraftMeta {
        return DefaultMeta(
            when {
                majorLegacy >= 11900 -> {
                    NMSDataWatcherItem(
                        NMSDataWatcherObject(index, NMSDataWatcherRegistry.ROTATIONS),
                        NMSVector3f(value.x.toFloat(), value.y.toFloat(), value.z.toFloat())
                    )
                }

                majorLegacy >= 11300 -> {
                    NMS13DataWatcherItem(
                        NMS13DataWatcherObject(index, NMS13DataWatcherRegistry.k),
                        NMS13Vector3f(value.x.toFloat(), value.y.toFloat(), value.z.toFloat())
                    )
                }

                else -> {
                    NMS12DataWatcherItem(
                        NMS12DataWatcherObject(index, NMS12DataWatcherRegistry.i),
                        NMS12Vector3f(value.x.toFloat(), value.y.toFloat(), value.z.toFloat())
                    )
                }
            }
        )
    }

    override fun createVillagerDataMeta(index: Int, villagerData: VillagerData): MinecraftMeta {
        return DefaultMeta(
            if (majorLegacy >= 11900) {
                val villagerType = when (villagerData.type) {
                    VillagerData.Type.DESERT -> NMSVillagerType.DESERT
                    VillagerData.Type.JUNGLE -> NMSVillagerType.JUNGLE
                    VillagerData.Type.PLAINS -> NMSVillagerType.PLAINS
                    VillagerData.Type.SAVANNA -> NMSVillagerType.SAVANNA
                    VillagerData.Type.SNOW -> NMSVillagerType.SNOW
                    VillagerData.Type.SWAMP -> NMSVillagerType.SWAMP
                    VillagerData.Type.TAIGA -> NMSVillagerType.TAIGA
                }
                val villagerProfession = when (villagerData.profession) {
                    VillagerData.Profession.NONE -> NMSVillagerProfession.NONE
                    VillagerData.Profession.ARMORER -> NMSVillagerProfession.ARMORER
                    VillagerData.Profession.BUTCHER -> NMSVillagerProfession.BUTCHER
                    VillagerData.Profession.CARTOGRAPHER -> NMSVillagerProfession.CARTOGRAPHER
                    VillagerData.Profession.CLERIC -> NMSVillagerProfession.CLERIC
                    VillagerData.Profession.FARMER -> NMSVillagerProfession.FARMER
                    VillagerData.Profession.FISHERMAN -> NMSVillagerProfession.FISHERMAN
                    VillagerData.Profession.FLETCHER -> NMSVillagerProfession.FLETCHER
                    VillagerData.Profession.LEATHERWORKER -> NMSVillagerProfession.LEATHERWORKER
                    VillagerData.Profession.LIBRARIAN -> NMSVillagerProfession.LIBRARIAN
                    VillagerData.Profession.MASON -> NMSVillagerProfession.MASON
                    VillagerData.Profession.NITWIT -> NMSVillagerProfession.NITWIT
                    VillagerData.Profession.SHEPHERD -> NMSVillagerProfession.SHEPHERD
                    VillagerData.Profession.TOOLSMITH -> NMSVillagerProfession.TOOLSMITH
                    VillagerData.Profession.WEAPONSMITH -> NMSVillagerProfession.WEAPONSMITH
                }
                NMSDataWatcherItem(NMSDataWatcherObject(index, NMSDataWatcherRegistry.VILLAGER_DATA), NMSVillagerData(villagerType, villagerProfession, 1))
            } else {
                val villagerType = when (villagerData.type) {
                    VillagerData.Type.DESERT -> NMS16VillagerType.DESERT
                    VillagerData.Type.JUNGLE -> NMS16VillagerType.JUNGLE
                    VillagerData.Type.PLAINS -> NMS16VillagerType.PLAINS
                    VillagerData.Type.SAVANNA -> NMS16VillagerType.SAVANNA
                    VillagerData.Type.SNOW -> NMS16VillagerType.SNOW
                    VillagerData.Type.SWAMP -> NMS16VillagerType.SWAMP
                    VillagerData.Type.TAIGA -> NMS16VillagerType.TAIGA
                }
                val villagerProfession = when (villagerData.profession) {
                    VillagerData.Profession.NONE -> NMS16VillagerProfession.NONE
                    VillagerData.Profession.ARMORER -> NMS16VillagerProfession.ARMORER
                    VillagerData.Profession.BUTCHER -> NMS16VillagerProfession.BUTCHER
                    VillagerData.Profession.CARTOGRAPHER -> NMS16VillagerProfession.CARTOGRAPHER
                    VillagerData.Profession.CLERIC -> NMS16VillagerProfession.CLERIC
                    VillagerData.Profession.FARMER -> NMS16VillagerProfession.FARMER
                    VillagerData.Profession.FISHERMAN -> NMS16VillagerProfession.FISHERMAN
                    VillagerData.Profession.FLETCHER -> NMS16VillagerProfession.FLETCHER
                    VillagerData.Profession.LEATHERWORKER -> NMS16VillagerProfession.LEATHERWORKER
                    VillagerData.Profession.LIBRARIAN -> NMS16VillagerProfession.LIBRARIAN
                    VillagerData.Profession.MASON -> NMS16VillagerProfession.MASON
                    VillagerData.Profession.NITWIT -> NMS16VillagerProfession.NITWIT
                    VillagerData.Profession.SHEPHERD -> NMS16VillagerProfession.SHEPHERD
                    VillagerData.Profession.TOOLSMITH -> NMS16VillagerProfession.TOOLSMITH
                    VillagerData.Profession.WEAPONSMITH -> NMS16VillagerProfession.WEAPONSMITH
                }
                NMS16DataWatcherItem(NMS16DataWatcherObject(index, NMS16DataWatcherRegistry.q), NMS16VillagerData(villagerType, villagerProfession, 1))
            }
        )
    }

    override fun createPoseMeta(index: Int, pose: BukkitPose): MinecraftMeta {
        return DefaultMeta(
            if (majorLegacy >= 11900) {
                NMSDataWatcherItem(
                    NMSDataWatcherObject(index, NMSDataWatcherRegistry.POSE),
                    when (pose) {
                        BukkitPose.STANDING -> NMSEntityPose.STANDING
                        BukkitPose.FALL_FLYING -> NMSEntityPose.FALL_FLYING
                        BukkitPose.SLEEPING -> NMSEntityPose.SLEEPING
                        BukkitPose.SWIMMING -> NMSEntityPose.SWIMMING
                        BukkitPose.SPIN_ATTACK -> NMSEntityPose.SPIN_ATTACK
                        BukkitPose.CROUCHING -> NMSEntityPose.CROUCHING
                        BukkitPose.DYING -> NMSEntityPose.DYING
                        BukkitPose.LONG_JUMPING -> NMSEntityPose.LONG_JUMPING
                        BukkitPose.CROAKING -> NMSEntityPose.CROAKING
                        BukkitPose.USING_TONGUE -> NMSEntityPose.USING_TONGUE
                        BukkitPose.ROARING -> NMSEntityPose.ROARING
                        BukkitPose.SNIFFING -> NMSEntityPose.SNIFFING
                        BukkitPose.DIGGING -> NMSEntityPose.DIGGING
                        BukkitPose.EMERGING -> NMSEntityPose.EMERGING
                    }
                )
            } else {
                NMS16DataWatcherItem(
                    NMS16DataWatcherObject(index, NMS16DataWatcherRegistry.s),
                    when (pose) {
                        BukkitPose.STANDING -> NMS16EntityPose.STANDING
                        BukkitPose.FALL_FLYING -> NMS16EntityPose.FALL_FLYING
                        BukkitPose.SLEEPING -> NMS16EntityPose.SLEEPING
                        BukkitPose.SWIMMING -> NMS16EntityPose.SWIMMING
                        BukkitPose.SPIN_ATTACK -> NMS16EntityPose.SPIN_ATTACK
                        BukkitPose.CROUCHING -> NMS16EntityPose.CROUCHING
                        BukkitPose.DYING -> NMS16EntityPose.DYING
                        else -> NMS16EntityPose.STANDING
                    }
                )
            }
        )
    }

    override fun createCatVariantMeta(index: Int, type: Any): MinecraftMeta {
        return if (majorLegacy >= 11903) {
            DefaultMeta(NMSJ17.instance.createCatVariantMeta(index, type as Cat.Type))
        } else {
            DefaultMeta(
                NMSDataWatcherItem(
                    NMSDataWatcherObject(index, NMSDataWatcherRegistry.CAT_VARIANT),
                    when (type.toString()) {
                        "TABBY" -> NMSCatVariant.TABBY
                        "BLACK" -> NMSCatVariant.BLACK
                        "RED" -> NMSCatVariant.RED
                        "SIAMESE" -> NMSCatVariant.SIAMESE
                        "BRITISH_SHORTHAIR" -> NMSCatVariant.BRITISH_SHORTHAIR
                        "CALICO" -> NMSCatVariant.CALICO
                        "PERSIAN" -> NMSCatVariant.PERSIAN
                        "RAGDOLL" -> NMSCatVariant.RAGDOLL
                        "WHITE" -> NMSCatVariant.WHITE
                        "JELLIE" -> NMSCatVariant.JELLIE
                        "ALL_BLACK" -> NMSCatVariant.ALL_BLACK
                        else -> NMSCatVariant.TABBY
                    }
                )
            )
        }
    }

    override fun createFrogVariantMeta(index: Int, type: Any): MinecraftMeta {
        return DefaultMeta(
            NMSDataWatcherItem(
                NMSDataWatcherObject(index, NMSDataWatcherRegistry.FROG_VARIANT),
                when (type.toString()) {
                    "TEMPERATE" -> NMSFrogVariant.TEMPERATE
                    "WARM" -> NMSFrogVariant.WARM
                    "COLD" -> NMSFrogVariant.COLD
                    else -> NMSFrogVariant.TEMPERATE
                }
            )
        )
    }

    override fun createPaintingVariantMeta(index: Int, type: Any): MinecraftMeta {
        return DefaultMeta(
            NMSDataWatcherItem(
                NMSDataWatcherObject(index, NMSDataWatcherRegistry.PAINTING_VARIANT),
                CraftArt19.BukkitToNotch(type as Art)
            )
        )
    }

    override fun createUUIDMeta(index: Int, value: UUID): MinecraftMeta {
        return DefaultMeta(
            when {
                majorLegacy >= 11900 -> {
                    NMSDataWatcherItem(NMSDataWatcherObject(index, NMSDataWatcherRegistry.OPTIONAL_UUID), Optional.of(value))
                }

                majorLegacy >= 11300 -> {
                    NMS13DataWatcherItem(NMS13DataWatcherObject(index, NMS13DataWatcherRegistry.o), Optional.of(value))
                }

                else -> {
                    NMS11DataWatcherItem(NMS11DataWatcherObject(index, NMS11DataWatcherRegistry.m), com.google.common.base.Optional.of(value))
                }
            }
        )
    }

    override fun createVector3Meta(index: Int, value: Vector): MinecraftMeta {
        return DefaultMeta(NMSJ17.instance.createVector3Meta(index, value))
    }

    override fun createQuaternionMeta(index: Int, value: Quat): MinecraftMeta {
        return DefaultMeta(NMSJ17.instance.createQuaternionMeta(index, value))
    }

    override fun createSnifferStateMeta(index: Int, state: AdySniffer.State): MinecraftMeta {
        return DefaultMeta(NMSJ17.instance.createSnifferStateMeta(index, state))
    }

    fun craftChatMessageFromString(message: String): Any? {
        return CraftChatMessage19.fromJSON(message)
    }
}