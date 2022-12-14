package ink.ptms.adyeshach.impl.nms

import ink.ptms.adyeshach.core.*
import ink.ptms.adyeshach.core.bukkit.BukkitParticles
import ink.ptms.adyeshach.core.bukkit.BukkitPose
import ink.ptms.adyeshach.core.bukkit.data.EmptyVector
import ink.ptms.adyeshach.core.bukkit.data.VillagerData
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.impl.nms.parser.*
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Art
import org.bukkit.entity.Cat
import org.bukkit.entity.Frog
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import org.bukkit.util.EulerAngle
import org.bukkit.util.Vector
import taboolib.module.nms.MinecraftVersion
import java.util.*

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.DefaultMinecraftEntityMetadataHandler
 *
 * @author 坏黑
 * @since 2022/6/28 00:09
 */
class DefaultMinecraftEntityMetadataHandler : MinecraftEntityMetadataHandler {

    val majorLegacy = MinecraftVersion.majorLegacy

    val intParser = IntParser()
    val byteParser = ByteParser()
    val floatParser = FloatParser()
    val booleanParser = BooleanParser()
    val registeredParser = LinkedHashMap<Class<*>, MinecraftMetadataParser<*>>()

    val helper: MinecraftHelper
        get() = Adyeshach.api().getMinecraftAPI().getHelper()

    init {
        addParser(String::class.java, StringParser())
        addParser(Vector::class.java, BlockPosParser())
        addParser(ItemStack::class.java, ItemStackParser())
        addParser(EulerAngle::class.java, EulerAngleParser())
        addParser(MaterialData::class.java, BlockStateParser())
        addParser(VillagerData::class.java, VillagerDataParser())
        addParser(TextComponent::class.java, TextComponentParser())
        addParser(BukkitParticles::class.java, BukkitParticleParser())
        addParser(BukkitPose::class.java, BukkitPoseParser())
        // 1.19+
        if (MinecraftVersion.majorLegacy >= 11900) {
            addParser(Cat.Type::class.java, CatVariantParser())
            addParser(Frog.Variant::class.java, FrogVariantParser())
            addParser(Art::class.java, PaintingVariantParser())
        }
    }

    override fun addParser(type: Class<*>, metadataParser: MinecraftMetadataParser<*>) {
        registeredParser[type] = metadataParser
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getParser(data: T): MinecraftMetadataParser<T>? {
        when (data) {
            is Int -> return intParser as MinecraftMetadataParser<T>
            is Byte -> return byteParser as MinecraftMetadataParser<T>
            is Float -> return floatParser as MinecraftMetadataParser<T>
            is Boolean -> return booleanParser as MinecraftMetadataParser<T>
        }
        registeredParser.entries.forEach { (k, v) ->
            if (k.isAssignableFrom((data as Any).javaClass)) {
                return v as MinecraftMetadataParser<T>
            }
        }
        return null
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

    override fun createOptionalComponentMeta(index: Int, text: String?): MinecraftMeta {
        return DefaultMeta(
            when {
                majorLegacy >= 11900 -> {
                    NMSDataWatcherItem(
                        NMSDataWatcherObject(index, NMSDataWatcherRegistry.OPTIONAL_COMPONENT),
                        Optional.ofNullable(if (text == null) null else craftChatMessageFromString(text) as NMSIChatBaseComponent)
                    )
                }
                majorLegacy >= 11400 -> {
                    NMS16DataWatcherItem(
                        NMS16DataWatcherObject(index, NMS16DataWatcherRegistry.f),
                        Optional.ofNullable(if (text == null) null else craftChatMessageFromString(text) as NMS16IChatBaseComponent)
                    )
                }
                else -> {
                    NMS12DataWatcherItem(NMS12DataWatcherObject(index, NMS12DataWatcherRegistry.d), text ?: "")
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

    override fun createBlockStateMeta(index: Int, blockData: MaterialData?): MinecraftMeta {
        return DefaultMeta(
            when {
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
        return DefaultMeta(
            when {
                majorLegacy >= 11900 -> {
                    NMSDataWatcherItem(NMSDataWatcherObject(index, NMSDataWatcherRegistry.PARTICLE), param as NMSParticleParam)
                }
                else -> {
                    NMS16DataWatcherItem(NMS16DataWatcherObject(index, NMS16DataWatcherRegistry.j), param as NMS16ParticleParam)
                }
            }
        )
    }

    override fun createBlockPosMeta(index: Int, vector: Vector?): MinecraftMeta {
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

    override fun createCatVariantMeta(index: Int, type: Cat.Type): MinecraftMeta {
        return DefaultMeta(
            NMSDataWatcherItem(
                NMSDataWatcherObject(index, NMSDataWatcherRegistry.CAT_VARIANT),
                when (type) {
                    Cat.Type.TABBY -> NMSCatVariant.TABBY
                    Cat.Type.BLACK -> NMSCatVariant.BLACK
                    Cat.Type.RED -> NMSCatVariant.RED
                    Cat.Type.SIAMESE -> NMSCatVariant.SIAMESE
                    Cat.Type.BRITISH_SHORTHAIR -> NMSCatVariant.BRITISH_SHORTHAIR
                    Cat.Type.CALICO -> NMSCatVariant.CALICO
                    Cat.Type.PERSIAN -> NMSCatVariant.PERSIAN
                    Cat.Type.RAGDOLL -> NMSCatVariant.RAGDOLL
                    Cat.Type.WHITE -> NMSCatVariant.WHITE
                    Cat.Type.JELLIE -> NMSCatVariant.JELLIE
                    Cat.Type.ALL_BLACK -> NMSCatVariant.ALL_BLACK
                }
            )
        )
    }

    override fun createFrogVariantMeta(index: Int, type: Frog.Variant): MinecraftMeta {
        return DefaultMeta(
            NMSDataWatcherItem(
                NMSDataWatcherObject(index, NMSDataWatcherRegistry.FROG_VARIANT),
                when (type) {
                    Frog.Variant.TEMPERATE -> NMSFrogVariant.TEMPERATE
                    Frog.Variant.WARM -> NMSFrogVariant.WARM
                    Frog.Variant.COLD -> NMSFrogVariant.COLD
                }
            )
        )
    }

    override fun createPaintingVariantMeta(index: Int, type: Art): MinecraftMeta {
        return DefaultMeta(
            NMSDataWatcherItem(
                NMSDataWatcherObject(index, NMSDataWatcherRegistry.PAINTING_VARIANT),
                CraftArt19.BukkitToNotch(type)
            )
        )
    }

    fun craftChatMessageFromString(message: String): Any? {
        return CraftChatMessage19.fromString(message)[0]
    }
}