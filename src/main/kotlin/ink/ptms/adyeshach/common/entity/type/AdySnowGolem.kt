package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdySnowGolem : AdyMob(EntityTypes.SNOW_GOLEM) {

    init {
        registerMetaByteMask(at(11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11), "hasPumpkinHat", 0x10, true)
        registerMetaByteMask(at(11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11), "hasNoPumpkinHat", 0x00)
    }

    fun setPumpkinHat(value: Boolean) {
        setMetadata("hasPumpkinHat", value)
        setMetadata("hasNoPumpkinHat", !value)
    }

    fun hasPumpkinHat(): Boolean {
        return getMetadata("hasPumpkinHat")
    }
}