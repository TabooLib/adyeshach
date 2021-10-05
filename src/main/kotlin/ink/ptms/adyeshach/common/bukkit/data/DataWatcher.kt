package ink.ptms.adyeshach.common.bukkit.data

import com.google.common.base.Enums
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.bukkit.BukkitParticles
import ink.ptms.adyeshach.common.bukkit.BukkitPose
import ink.ptms.adyeshach.common.util.serializer.Serializer
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Material
import org.bukkit.entity.Villager
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import org.bukkit.util.EulerAngle
import org.bukkit.util.NumberConversions
import taboolib.common.util.Vector
import taboolib.library.xseries.XMaterial

abstract class DataWatcher {

    abstract fun parse(value: Any): Any

    abstract fun createMetadata(index: Int, value: Any): Any

    protected fun Any.toInt(): Int = NumberConversions.toInt(this)

    protected fun Any.toByte(): Byte = NumberConversions.toByte(this)

    protected fun Any.toDouble(): Double = NumberConversions.toDouble(this)

    object DataInt : DataWatcher() {

        override fun parse(value: Any): Any {
            return NumberConversions.toInt(value)
        }

        override fun createMetadata(index: Int, value: Any): Any {
            return NMS.INSTANCE.getMetaEntityInt(index, NumberConversions.toInt(value))
        }
    }

    object DataByte : DataWatcher() {

        override fun parse(value: Any): Any {
            return NumberConversions.toByte(value)
        }

        override fun createMetadata(index: Int, value: Any): Any {
            return NMS.INSTANCE.getMetaEntityByte(index, NumberConversions.toByte(value))
        }
    }

    object DataFloat : DataWatcher() {

        override fun parse(value: Any): Any {
            return NumberConversions.toFloat(value)
        }

        override fun createMetadata(index: Int, value: Any): Any {
            return NMS.INSTANCE.getMetaEntityFloat(index, NumberConversions.toFloat(value))
        }
    }

    object DataString : DataWatcher() {

        override fun parse(value: Any): Any {
            return value as String
        }

        override fun createMetadata(index: Int, value: Any): Any {
            return NMS.INSTANCE.getMetaEntityString(index, value as String)
        }
    }

    object DataBoolean : DataWatcher() {

        override fun parse(value: Any): Any {
            return value as Boolean
        }

        override fun createMetadata(index: Int, value: Any): Any {
            return NMS.INSTANCE.getMetaEntityBoolean(index, value as Boolean)
        }
    }

    object DataPose : DataWatcher() {

        override fun parse(value: Any): Any {
            return if (value is String) Enums.getIfPresent(BukkitPose::class.java, value).or(BukkitPose.STANDING) else value as BukkitPose
        }

        override fun createMetadata(index: Int, value: Any): Any {
            return NMS.INSTANCE.getMetaEntityPose(index, parse(value) as BukkitPose)
        }
    }

    object DataVector : DataWatcher() {

        override fun parse(value: Any): Any {
            return if (value is Map<*, *>) EulerAngle(value["x"]!!.toDouble(), value["y"]!!.toDouble(), value["z"]!!.toDouble()) else value as EulerAngle
        }

        override fun createMetadata(index: Int, value: Any): Any {
            return NMS.INSTANCE.getMetaEntityEulerAngle(index, parse(value) as EulerAngle)
        }
    }

    object DataParticle : DataWatcher() {

        override fun parse(value: Any): Any {
            return if (value is String) BukkitParticles.valueOf(value) else value as BukkitParticles
        }

        override fun createMetadata(index: Int, value: Any): Any {
            return NMS.INSTANCE.getMetaEntityParticle(index, parse(value) as BukkitParticles)
        }
    }

    object DataIChatBaseComponent : DataWatcher() {

        override fun parse(value: Any): Any {
            return if (value is TextComponent) value.text else value.toString()
        }

        override fun createMetadata(index: Int, value: Any): Any {
            val text = parse(value) as String
            return NMS.INSTANCE.getMetaEntityChatBaseComponent(index, if (text.isEmpty()) null else text)
        }
    }

    object DataItemStack : DataWatcher() {

        override fun parse(value: Any): Any {
            return when (value) {
                is ItemStack -> value
                is String -> Serializer.toItemStack(value)
                else -> ItemStack(Material.BEDROCK)
            }
        }

        override fun createMetadata(index: Int, value: Any): Any {
            return NMS.INSTANCE.getMetaItem(index, parse(value) as ItemStack)
        }
    }

    object DataBlockData : DataWatcher() {

        override fun parse(value: Any): Any {
            return if (value is Map<*, *>) {
                val material = XMaterial.matchXMaterial(value["type"].toString()).orElse(XMaterial.STONE).parseMaterial()!!
                MaterialData(material, value["data"]!!.toByte())
            } else {
                value as MaterialData
            }
        }

        override fun createMetadata(index: Int, value: Any): Any {
            val material = parse(value) as MaterialData
            return NMS.INSTANCE.getMetaEntityBlockData(index, if (material.itemType == Material.AIR) null else material)
        }
    }

    object DataVillagerData : DataWatcher() {

        override fun parse(value: Any): Any {
            return if (value is Map<*, *>) {
                VillagerData(Enums.getIfPresent(Villager.Type::class.java, value["type"]!!.toString()).get(), Enums.getIfPresent(Villager.Profession::class.java, value["profession"]!!.toString()).get())
            } else {
                value as VillagerData
            }
        }

        override fun createMetadata(index: Int, value: Any): Any {
            return NMS.INSTANCE.getMetaVillagerData(index, parse(value) as VillagerData)
        }
    }

    object DataPosition : DataWatcher() {

        override fun parse(value: Any): Any {
            return if (value is Map<*, *>) {
                if (value["empty"] as Boolean) {
                    VectorNull()
                } else {
                    Vector(value["x"]!!.toInt(), value["y"]!!.toInt(), value["z"]!!.toInt())
                }
            } else {
                value as Vector
            }
        }

        override fun createMetadata(index: Int, value: Any): Any {
            return NMS.INSTANCE.getMetaEntityVector(index, parse(value) as Vector)
        }
    }
}