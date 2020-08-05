package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.type.EntityTypes
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-04 19:30
 */
class AdyBlaze(owner: Player) : AdyEntityLiving(owner, EntityTypes.BLAZE) {

    /**
     * 1.13,1.12 -> Index 12
     * 1.9 -> Index 11
     */
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