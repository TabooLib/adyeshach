package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyPolarBear : AdyEntityAgeable(EntityTypes.POLAR_BEAR) {

    init {
        registerMeta(at(11500 to 16, 11400 to 15, 11000 to 13), "isStanding", false)
    }

    fun isStanding(): Boolean {
        return getMetadata("isStanding")
    }

    fun setStanding(value: Boolean) {
        setMetadata("isStanding", value)
    }

}