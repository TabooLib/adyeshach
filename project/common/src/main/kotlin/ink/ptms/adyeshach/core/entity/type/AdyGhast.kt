package ink.ptms.adyeshach.core.entity.type

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyGhast : AdyMob {

    fun isAttacking(): Boolean {
        return getMetadata("isAttacking")
    }

    fun setAttacking(isAttacking: Boolean) {
        setMetadata("isAttacking", isAttacking)
    }
}