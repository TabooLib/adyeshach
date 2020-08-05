package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.api.nms.NMS
import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-04 19:30
 */
class AdyBlaze(owner: Player) : AdyEntityLiving(owner, EntityTypes.BLAZE) {

    init {
        registerMetaByteMask(15, "fire", 0x01)
    }

    fun setFire(value: Boolean) {
        setMetadata("fire", value)
    }

    fun isFire(): Boolean {
        return getMetadata("fire")
    }
}