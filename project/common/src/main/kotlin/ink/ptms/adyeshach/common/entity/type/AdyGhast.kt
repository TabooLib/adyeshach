package ink.ptms.adyeshach.common.entity.type

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Suppress("SpellCheckingInspection")
abstract class AdyGhast : AdyMob() {

    open fun isAttacking(): Boolean {
        return getMetadata("isAttacking")
    }

    open fun setAttacking(isAttacking: Boolean) {
        setMetadata("isAttacking", isAttacking)
    }
}