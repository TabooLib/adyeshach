package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
class AdyGhast(v2: ink.ptms.adyeshach.core.entity.EntityInstance):  AdyMob(EntityTypes.GHAST, v2) {

    fun isAttacking(): Boolean {
        return getMetadata("isAttacking")
    }

    fun setAttacking(isAttacking: Boolean) {
        setMetadata("isAttacking", isAttacking)
    }
}