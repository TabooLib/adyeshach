package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @since 2020-08-04 18:28
 */
@Deprecated("Outdated but usable")
abstract class AdyEntityAgeable(entityTypes: EntityTypes, v2: ink.ptms.adyeshach.core.entity.EntityInstance) : AdyMob(entityTypes, v2) {

    open fun setBaby(value: Boolean) {
        setMetadata("isBaby", value)
    }

    open fun isBaby(): Boolean {
        return getMetadata("isBaby")
    }
}