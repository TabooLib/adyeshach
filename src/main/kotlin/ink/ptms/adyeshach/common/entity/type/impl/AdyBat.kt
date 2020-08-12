package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.api.nms.NMS
import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-04 19:30
 */
class AdyBat() : AdyEntityLiving(EntityTypes.BAT) {

    /**
     * 1.13,1.12 Index->12
     * 1.9 Index->11
     */
    init {
        registerMetaByteMask(at(11600.to(15), 11000.to(12), 10900.to(11)), "isHanging", 0x01)
    }

    fun setHanging(value: Boolean) {
        setMetadata("isHanging", value)
    }

    fun isHanging(): Boolean {
        return getMetadata("isHanging")
    }
}