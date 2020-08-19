package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyVex() : AdyMob(EntityTypes.VEX) {

    init {
        registerMeta(at(11500 to 15, 11400 to 14, 11300 to 13, 11100 to 12), "attackMode", false)
    }

    fun isAttackMode(): Boolean {
        return getMetadata("attackMode")
    }

    fun getAttackMode(attackMode: Int) {
        return setMetadata("attackMode", attackMode)
    }
}