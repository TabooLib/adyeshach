package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.type.EntityTypes

/**
 * @Author sky
 * @Since 2020-08-04 19:30
 */
class AdyDolphin() : AdyEntityLiving(EntityTypes.DOLPHIN) {

    init {
        /**
         * 1.15 -> 16, 17
         * 1.14 -> 15, 16
         * 1.13 -> 13, 14
         */
        registerMeta(at(11500 to 16, 11400 to 15, 11300 to 13), "findTreasure", false)
        registerMeta(at(11500 to 17, 11400 to 16, 11300 to 14), "hasFish", false)
    }

    fun setFindTreasure(value: Boolean) {
        setMetadata("findTreasure", value)
    }

    fun isFindTreasure(): Boolean {
        return getMetadata("findTreasure")
    }

    fun setHasFish(value: Boolean) {
        setMetadata("hasFish", value)
    }

    fun isHasFish(): Boolean {
        return getMetadata("hasFish")
    }
}