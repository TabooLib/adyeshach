package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.bukkit.BukkitParticles
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
class AdyBat(owner: Player) : AdyEntityLiving(owner, EntityTypes.BAT), MetadataExtend {

    init {
        properties = BatProperties()
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

    private fun getProperties(): BatProperties = properties as BatProperties

    private class BatProperties : EntityProperties() {

        @Expose
        var hanging = false
    }
}