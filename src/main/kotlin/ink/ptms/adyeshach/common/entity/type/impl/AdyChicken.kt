package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.MetadataExtend
import ink.ptms.adyeshach.common.entity.element.EntityProperties
import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.nms.NMS
import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-04 19:30
 */
class AdyChicken(owner: Player) : AdyEntityLiving(owner, EntityTypes.CHICKEN), MetadataExtend {

    init {
        properties = ChickenProperties()
    }

    fun setHanging(value: Boolean) {
        getProperties().hanging = value
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityByte(15, if (value) 0x01 else 0x00))
    }

    fun isHanging(): Boolean {
        return getProperties().hanging
    }

    override fun metadata(): List<Any> {
        return getProperties().run {
            listOf(NMS.INSTANCE.getMetaEntityByte(15, if (hanging) 0x01 else 0x00))
        }
    }

    private fun getProperties(): ChickenProperties = properties as ChickenProperties

    private class ChickenProperties : EntityProperties() {

        @Expose
        var hanging = false
    }
}