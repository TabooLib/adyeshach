package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @Author sky
 * @Since 2020-08-04 19:30
 */
class AdyBat : AdyMob(EntityTypes.BAT) {

    /**
     * 1.13,1.12 Index->12
     * 1.9 Index->11
     */
    init {
        registerMetaByteMask(at(11700 to 16, 11600 to 15, 11000 to 12, 10900 to 11), "isHanging", 0x01)
    }

    fun setHanging(value: Boolean) {
        setMetadata("isHanging", value)
    }

    fun isHanging(): Boolean {
        return getMetadata("isHanging")
    }
}