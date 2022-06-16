package ink.ptms.adyeshach.common.entity.type

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
abstract class AdyVex : AdyMob() {

    open fun isAttackMode(): Boolean {
        return getMetadata("attackMode")
    }

    open fun getAttackMode(attackMode: Int) {
        setMetadata("attackMode", attackMode)
    }
}