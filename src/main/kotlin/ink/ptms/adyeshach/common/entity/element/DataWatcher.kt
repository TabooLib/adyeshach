package ink.ptms.adyeshach.common.entity.element

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.bukkit.BukkitParticles
import io.izzel.taboolib.util.chat.TextComponent
import org.bukkit.util.EulerAngle

abstract class DataWatcher {

    abstract fun getMetadata(index: Int, value: Any): Any

    class DataInt : DataWatcher() {

        override fun getMetadata(index: Int, value: Any): Any {
            return NMS.INSTANCE.getMetaEntityInt(index, value as Int)
        }
    }

    class DataFloat : DataWatcher() {

        override fun getMetadata(index: Int, value: Any): Any {
            return NMS.INSTANCE.getMetaEntityFloat(index, value as Float)
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

    class DataParticle : DataWatcher() {

        override fun getMetadata(index: Int, value: Any): Any {
            return NMS.INSTANCE.getMetaEntityParticle(index, value as BukkitParticles)
        }
    }

    class DataByte : DataWatcher() {

        override fun getMetadata(index: Int, value: Any): Any {
            return NMS.INSTANCE.getMetaEntityByte(index, value as Byte)
        }
    }

    class DataVector : DataWatcher() {

        override fun getMetadata(index: Int, value: Any): Any {
            return NMS.INSTANCE.getMetaEntityVector(index, value as EulerAngle)
        }
    }

    class DataIChatBaseComponent : DataWatcher() {

        override fun getMetadata(index: Int, value: Any): Any {
            return NMS.INSTANCE.getMetaEntityChatBaseComponent(index, (value as TextComponent).text)
        }
    }
}