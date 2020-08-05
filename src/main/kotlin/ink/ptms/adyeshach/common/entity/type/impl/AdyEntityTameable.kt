package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.api.nms.NMS
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-04 18:28
 */
abstract class AdyEntityTameable(owner: Player, entityTypes: EntityTypes) : AdyEntityAgeable(owner, entityTypes) {

    init {
        registerMetaByteMask(16, "isSitting", 0x01)
        registerMetaByteMask(16, "isAngry", 0x02)
        registerMetaByteMask(16, "isTamed", 0x04)
    }

    fun setSitting(value: Boolean) {
        setMetadata("isSitting", value)
    }

    fun isSitting(): Boolean {
        return getMetadata("isSitting")
    }

    fun setAngry(value: Boolean) {
        setMetadata("isAngry", value)
    }

    fun isAngry(): Boolean {
        return getMetadata("isAngry")
    }

    fun setTamed(value: Boolean) {
        setMetadata("isTamed", value)
    }

    fun isTamed(): Boolean {
        return getMetadata("isTamed")
    }
}