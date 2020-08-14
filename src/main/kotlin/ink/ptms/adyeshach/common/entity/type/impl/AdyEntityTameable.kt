package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.type.EntityTypes

/**
 * @Author sky
 * @Since 2020-08-04 18:28
 */
abstract class AdyEntityTameable(entityTypes: EntityTypes) : AdyEntityAgeable(entityTypes) {

    init {
        /**
         * 1.9 -> 12
         * 1.10 -> 13
         * 1.14 -> 15
         * 1.15 -> 16
         */
        registerMetaByteMask(at(11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "isSitting", 0x01)
        registerMetaByteMask(at(11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "isAngry", 0x02)
        registerMetaByteMask(at(11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "isTamed", 0x04)
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