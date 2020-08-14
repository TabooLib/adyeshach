package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.type.EntityTypes

/**
 * @Author sky
 * @Since 2020-08-04 19:30
 */
class AdyBlaze() : AdyEntityLiving(EntityTypes.BLAZE) {

    /**
     * 1.13,1.12 -> Index 12
     * 1.9 -> Index 11
     */
    init {
        registerMetaByteMask(at(11600.to(15), 11100.to(12), 10900.to(11)), "fire", 0x01)
    }

    fun setFire(value: Boolean) {
        setMetadata("fire", value)
    }

    fun isFire(): Boolean {
        return getMetadata("fire")
    }
}