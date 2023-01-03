package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
abstract class AdyRaider(entityTypes: EntityTypes, v2: ink.ptms.adyeshach.core.entity.EntityInstance) : AdyMob(entityTypes, v2) {

    fun isCelebrating(): Boolean {
        return getMetadata("isCelebrating")
    }

    fun setCelebrating(isCelebrating: Boolean) {
        setMetadata("isCelebrating", isCelebrating)
    }
}