package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.api.nms.NMS
import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-04 19:30
 */
class AdyBat(owner: Player) : AdyEntityLiving(owner, EntityTypes.BAT) {

    init {
        registerMetaByteMask(15, "isHanging", 0x01)
    }

    fun setHanging(value: Boolean) {
        setMetadata("isHanging", value)
    }

    fun isHanging(): Boolean {
        return getMetadata("isHanging")
    }
}