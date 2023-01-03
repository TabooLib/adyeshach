package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @since 2020-08-04 18:28
 */
@Deprecated("Outdated but usable")
abstract class AdyHorseChested(entityTypes: EntityTypes, v2: ink.ptms.adyeshach.core.entity.EntityInstance) : AdyHorseBase(entityTypes, v2) {

    override fun setHasChest(value: Boolean) {
        setMetadata("hasChest", value)
    }

    override fun isHasChest(): Boolean {
        return getMetadata("hasChest")
    }
}