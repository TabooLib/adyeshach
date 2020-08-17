package ink.ptms.adyeshach.common.entity.element

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.bukkit.BukkitParticles
import ink.ptms.adyeshach.common.util.serializer.Serializer
import io.izzel.taboolib.module.nms.impl.Position
import io.izzel.taboolib.util.chat.TextComponent
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import org.bukkit.util.EulerAngle
import org.bukkit.util.NumberConversions

abstract class DataWatcher {

    abstract fun parse(value: Any): Any

    abstract fun getMetadata(index: Int, value: Any): Any

    fun Any.toInt(): Int = NumberConversions.toInt(this)

    fun Any.toByte(): Byte = NumberConversions.toByte(this)

    fun Any.toDouble(): Double = NumberConversions.toDouble(this)

    class DataInt : DataWatcher() {

        override fun parse(value: Any): Any {
            return NumberConversions.toInt(value)
        }

        override fun getMetadata(index: Int, value: Any): Any {
            return NMS.INSTANCE.getMetaEntityInt(index, NumberConversions.toInt(value))
        }
    }

    class DataByte : DataWatcher() {

        override fun parse(value: Any): Any {
            return NumberConversions.toByte(value)
        }

        override fun getMetadata(index: Int, value: Any): Any {
            return NMS.INSTANCE.getMetaEntityByte(index, NumberConversions.toByte(value))
        }
    }

    class DataFloat : DataWatcher() {

        override fun parse(value: Any): Any {
            return NumberConversions.toFloat(value)
        }

        override fun getMetadata(index: Int, value: Any): Any {
            return NMS.INSTANCE.getMetaEntityFloat(index, NumberConversions.toFloat(value))
        }
    }

    class DataString : DataWatcher() {

        override fun parse(value: Any): Any {
            return value as String
        }

        override fun getMetadata(index: Int, value: Any): Any {
            return NMS.INSTANCE.getMetaEntityString(index, value as String)
        }
    }

    class DataBoolean : DataWatcher() {

        override fun parse(value: Any): Any {
            return value as Boolean
        }

        override fun getMetadata(index: Int, value: Any): Any {
            return NMS.INSTANCE.getMetaEntityBoolean(index, value as Boolean)
        }
    }

    class DataVector : DataWatcher() {

        override fun parse(value: Any): Any {
            return if (value is Map<*, *>) EulerAngle(value["x"]!!.toDouble(), value["y"]!!.toDouble(), value["z"]!!.toDouble()) else value as EulerAngle
        }

        override fun getMetadata(index: Int, value: Any): Any {
            return NMS.INSTANCE.getMetaEntityVector(index, parse(value) as EulerAngle)
        }
    }

    class DataParticle : DataWatcher() {

        override fun parse(value: Any): Any {
            return if (value is String) BukkitParticles.valueOf(value) else value as BukkitParticles
        }

        override fun getMetadata(index: Int, value: Any): Any {
            return NMS.INSTANCE.getMetaEntityParticle(index, parse(value) as BukkitParticles)
        }
    }

    class DataIChatBaseComponent : DataWatcher() {

        override fun parse(value: Any): Any {
            return if (value is TextComponent) value.text else value.toString()
        }

        override fun getMetadata(index: Int, value: Any): Any {
            val text = parse(value) as String
            return NMS.INSTANCE.getMetaEntityChatBaseComponent(index, if (text.isEmpty()) null else text)
        }
    }

    class DataItemStack : DataWatcher() {

        override fun parse(value: Any): Any {
            return when (value) {
                is ItemStack -> value
                is String -> Serializer.toItemStack(value)
                else -> ItemStack(Material.BEDROCK)
            }
        }

        override fun getMetadata(index: Int, value: Any): Any {
            return NMS.INSTANCE.getMetaItem(index, parse(value) as ItemStack)
        }
    }

    class DataBlockData : DataWatcher() {

        override fun parse(value: Any): Any {
            return if (value is Map<*, *>) MaterialData(Material.getMaterial(value["type"].toString()), value["data"]!!.toByte()) else value as MaterialData
        }

        override fun getMetadata(index: Int, value: Any): Any {
            val material = parse(value) as MaterialData
            return NMS.INSTANCE.getMetaEntityBlockData(index, if (material.itemType == Material.AIR) null else material)
        }
    }

    class DataPosition : DataWatcher() {

        override fun parse(value: Any): Any {
            return if (value is Map<*, *>) {
                if (value["empty"] as Boolean) {
                    NullPosition()
                } else {
                    Position(value["x"]!!.toInt(), value["y"]!!.toInt(), value["z"]!!.toInt())
                }
            } else {
                value as Position
            }
        }

        override fun getMetadata(index: Int, value: Any): Any {
            return NMS.INSTANCE.getMetaEntityPosition(index, parse(value) as Position)
        }
    }
}