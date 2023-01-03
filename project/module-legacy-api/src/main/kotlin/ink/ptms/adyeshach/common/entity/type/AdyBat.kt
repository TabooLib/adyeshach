package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @since 2020-08-04 19:30
 */
@Deprecated("Outdated but usable")
class AdyBat(v2: ink.ptms.adyeshach.core.entity.EntityInstance) : AdyMob(EntityTypes.BAT, v2) {

    fun setHanging(value: Boolean) {
        setMetadata("isHanging", value)
    }

    fun isHanging(): Boolean {
        return getMetadata("isHanging")
    }
}