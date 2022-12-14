package ink.ptms.adyeshach.core.entity.type

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyVex : AdyMob {

    fun isAttackMode(): Boolean {
        return getMetadata("attackMode")
    }

    fun getAttackMode(attackMode: Int) {
        setMetadata("attackMode", attackMode)
    }
}