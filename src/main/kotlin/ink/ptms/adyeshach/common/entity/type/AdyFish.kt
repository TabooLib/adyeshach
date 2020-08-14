package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @Author sky
 * @Since 2020-08-04 18:28
 */
abstract class AdyFish(entityTypes: EntityTypes) : AdyEntityLiving(entityTypes) {

    init {
        /**
         * 1.15 -> 15
         * 1.14 -> 14
         * 1.13 -> 12
         */
        registerMeta(at(11500 to 15, 11400 to 14, 11300 to 12), "fromBucket", false)
    }

    fun setFromBucket(value: Boolean) {
        setMetadata("fromBucket", value)
    }

    fun isFromBucket(): Boolean {
        return getMetadata("fromBucket")
    }
}