package ink.ptms.adyeshach.impl.nms

import ink.ptms.adyeshach.common.api.*
import ink.ptms.adyeshach.common.bukkit.BukkitParticles
import ink.ptms.adyeshach.common.bukkit.BukkitPose
import ink.ptms.adyeshach.common.bukkit.data.EmptyVector
import ink.ptms.adyeshach.common.bukkit.data.VillagerData
import ink.ptms.adyeshach.common.util.getEnumOrNull
import ink.ptms.adyeshach.impl.nms.parser.*
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Art
import org.bukkit.entity.Cat
import org.bukkit.entity.Frog
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import org.bukkit.util.EulerAngle
import org.bukkit.util.Vector
import taboolib.common.reflect.Reflex.Companion.getProperty
import taboolib.common.reflect.Reflex.Companion.invokeMethod
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.obcClass
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

                majorLegacy >= 11400 -> {
                    NMS16DataWatcherItem(
                        NMS16DataWatcherObject(index, NMS16DataWatcherRegistry.g),
                        CraftItemStack16.asNMSCopy(itemStack)
                    )
                }

                majorLegacy >= 11300 -> {
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

                majorLegacy >= 11400 -> {
                    NMS16DataWatcherItem(
                        NMS16DataWatcherObject(index, NMS16DataWatcherRegistry.h),
                        Optional.ofNullable(if (blockData == null) null else CraftMagicNumbers16.getBlock(blockData))
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

                majorLegacy >= 11400 -> {
                    NMS16DataWatcherItem(NMS16DataWatcherObject(index, NMS16DataWatcherRegistry.i), value)
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

                majorLegacy >= 11400 -> {
                    NMS16DataWatcherItem(
                        NMS16DataWatcherObject(index, NMS16DataWatcherRegistry.m),
                        Optional.ofNullable(if (vector == null || vector is EmptyVector) null else NMS16BlockPosition(vector.x, vector.y, vector.z))
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

                majorLegacy >= 11400 -> {
                    NMS16DataWatcherItem(
                        NMS16DataWatcherObject(index, NMS16DataWatcherRegistry.k),
                        NMS16Vector3f(value.x.toFloat(), value.y.toFloat(), value.z.toFloat())
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
                val villagerType = NMSVillagerType::class.java.getProperty<NMSVillagerType>(villagerData.type.name, fixed = true)
                val villagerProfession = NMSVillagerProfession::class.java.getProperty<NMSVillagerProfession>(villagerData.profession.name, fixed = true)
                NMSDataWatcherItem(NMSDataWatcherObject(index, NMSDataWatcherRegistry.VILLAGER_DATA), NMSVillagerData(villagerType, villagerProfession, 1))
            } else {
                val villagerType = NMS16VillagerType::class.java.getProperty<NMS16VillagerType>(villagerData.type.name, fixed = true)
                val villagerProfession = NMS16VillagerProfession::class.java.getProperty<NMS16VillagerProfession>(villagerData.profession.name, fixed = true)
                NMS16DataWatcherItem(NMS16DataWatcherObject(index, NMS16DataWatcherRegistry.q), NMS16VillagerData(villagerType, villagerProfession, 1))
            }
        )
    }

    override fun createPoseMeta(index: Int, pose: BukkitPose): MinecraftMeta {
        return DefaultMeta(
            if (majorLegacy >= 11900) {
                NMSDataWatcherItem(
                    NMSDataWatcherObject(index, NMSDataWatcherRegistry.POSE),
                    NMSEntityPose::class.java.getEnumOrNull(pose.name) ?: NMSEntityPose.STANDING
                )
            } else {
                NMS16DataWatcherItem(
                    NMS16DataWatcherObject(index, NMS16DataWatcherRegistry.s),
                    NMS16EntityPose::class.java.getEnumOrNull(pose.name) ?: NMS16EntityPose.STANDING
                )
            }
        )
    }

    override fun createCatVariantMeta(index: Int, type: Cat.Type): MinecraftMeta {
        return DefaultMeta(
            NMSDataWatcherItem(
                NMSDataWatcherObject(index, NMSDataWatcherRegistry.CAT_VARIANT),
                NMSCatVariant::class.java.getProperty(type.name, fixed = true)
            )
        )
    }

    override fun createFrogVariantMeta(index: Int, type: Frog.Variant): MinecraftMeta {
        return DefaultMeta(
            NMSDataWatcherItem(
                NMSDataWatcherObject(index, NMSDataWatcherRegistry.FROG_VARIANT),
                NMSFrogVariant::class.java.getProperty(type.name, fixed = true)
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
        return obcClass("util.CraftChatMessage").invokeMethod<Array<*>>("fromString", message, fixed = true)!![0]
    }
}