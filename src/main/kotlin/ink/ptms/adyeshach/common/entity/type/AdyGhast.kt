package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyGhast : AdyMob(EntityTypes.GHAST) {

    init {
        /*
        1.16,1.15
        15 ->Is attacking
        1.14
        14 ->Is attacking
        1.13,1.12,1.11,1.10
        12 ->Is attacking
        1.9
        11 ->Is attacking
         */
        registerMeta(at(11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11), "isAttacking", false)
    }

    fun isAttacking(): Boolean {
        return getMetadata("isAttacking")
    }

    fun setAttacking(isAttacking: Boolean) {
        setMetadata("isAttacking", isAttacking)
    }
}