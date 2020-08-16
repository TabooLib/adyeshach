package ink.ptms.adyeshach.common.entity.element

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.bukkit.BukkitParticles
import io.izzel.taboolib.util.chat.TextComponent
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.util.EulerAngle
import org.bukkit.util.NumberConversions

abstract class DataWatcher {

    abstract fun getMetadata(index: Int, value: Any): Any

    class DataInt : DataWatcher() {

        override fun getMetadata(index: Int, value: Any): Any {
            return NMS.INSTANCE.getMetaEntityInt(index, NumberConversions.toInt(value))
        }
    }

    class DataByte : DataWatcher() {

        override fun getMetadata(index: Int, value: Any): Any {
            return NMS.INSTANCE.getMetaEntityByte(index, NumberConversions.toByte(value))
        }
    }

    class DataFloat : DataWatcher() {

        override fun getMetadata(index: Int, value: Any): Any {
            return NMS.INSTANCE.getMetaEntityFloat(index, NumberConversions.toFloat(value))
        }
    }

    class DataString : DataWatcher() {

        override fun getMetadata(index: Int, value: Any): Any {
            return NMS.INSTANCE.getMetaEntityString(index, value as String)
        }
    }

    class DataBoolean : DataWatcher() {

        override fun getMetadata(index: Int, value: Any): Any {
            return NMS.INSTANCE.getMetaEntityBoolean(index, value as Boolean)
        }
    }

    class DataVector : DataWatcher() {

        override fun getMetadata(index: Int, value: Any): Any {
            if (value is Map<*, *>) {
                return NMS.INSTANCE.getMetaEntityVector(index, EulerAngle((value["x"] ?: 0.0) as Double, (value["y"] ?: 0.0) as Double, (value["z"] ?: 0.0) as Double))
            } else {
                return NMS.INSTANCE.getMetaEntityVector(index, value as EulerAngle)
            }
        }
    }

    class DataParticle : DataWatcher() {

        override fun getMetadata(index: Int, value: Any): Any {
            return NMS.INSTANCE.getMetaEntityParticle(index, if (value is String) BukkitParticles.valueOf(value) else value as BukkitParticles)
        }
    }

    class DataIChatBaseComponent : DataWatcher() {

        override fun getMetadata(index: Int, value: Any): Any {
            val text = if (value is TextComponent) value.text else value.toString()
            return NMS.INSTANCE.getMetaEntityChatBaseComponent(index, if (text.isEmpty()) null else text)
        }
    }

    class DataItemStack : DataWatcher() {

        override fun getMetadata(index: Int, value: Any): Any {
            val item = if (value is ItemStack) value else ItemStack(Material.BEDROCK)
            return NMS.INSTANCE.getMetaItem(index, item)
        }
    }

}