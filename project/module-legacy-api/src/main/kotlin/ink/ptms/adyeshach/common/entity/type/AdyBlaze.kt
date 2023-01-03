package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @since 2020-08-04 19:30
 */
@Deprecated("Outdated but usable")
class AdyBlaze(v2: ink.ptms.adyeshach.core.entity.EntityInstance) : AdyMob(EntityTypes.BLAZE, v2) {

    fun setFire(value: Boolean) {
        setMetadata("fire", value)
    }

    fun isFire(): Boolean {
        return getMetadata("fire")
    }
}