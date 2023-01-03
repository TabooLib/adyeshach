package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
class AdyVex(v2: ink.ptms.adyeshach.core.entity.EntityInstance):  AdyMob(EntityTypes.VEX, v2) {

    fun isAttackMode(): Boolean {
        return getMetadata("attackMode")
    }

    fun getAttackMode(attackMode: Int) {
        setMetadata("attackMode", attackMode)
    }
}