package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
class AdyPolarBear(v2: ink.ptms.adyeshach.core.entity.EntityInstance):  AdyEntityAgeable(EntityTypes.POLAR_BEAR, v2) {

    fun isStanding(): Boolean {
        return getMetadata("isStanding")
    }

    fun setStanding(value: Boolean) {
        setMetadata("isStanding", value)
    }

}