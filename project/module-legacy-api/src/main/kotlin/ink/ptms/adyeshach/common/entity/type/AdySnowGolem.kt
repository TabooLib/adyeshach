package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
class AdySnowGolem(v2: ink.ptms.adyeshach.core.entity.EntityInstance):  AdyMob(EntityTypes.SNOW_GOLEM, v2) {

    fun setPumpkinHat(value: Boolean) {
        setMetadata("hasPumpkinHat", value)
        setMetadata("hasNoPumpkinHat", !value)
    }

    fun hasPumpkinHat(): Boolean {
        return getMetadata("hasPumpkinHat")
    }
}