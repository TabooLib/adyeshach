package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @since 2020-08-04 18:28
 */
@Deprecated("Outdated but usable")
abstract class AdyEntityTameable(entityTypes: EntityTypes, v2: ink.ptms.adyeshach.core.entity.EntityInstance) : AdyEntityAgeable(entityTypes, v2) {

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