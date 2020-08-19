package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyPiglin() : AdyEntityLiving(EntityTypes.PIGLIN) {

    init {
        val index = at(11600 to 15)
        registerMeta(index + 0, "isImmuneToZombification", false)
        registerMeta(index + 1, "isBaby", false)
        registerMeta(index + 2, "isChargingCrossbow", false)
        registerMeta(index + 3, "isDancing", false)
    }

    fun setImmuneToZombification(isImmuneToZombification: Boolean) {
        setMetadata("isImmuneToZombification", isImmuneToZombification)
    }

    fun isImmuneToZombification(): Boolean {
        return getMetadata("isImmuneToZombification")
    }

    fun setBaby(isBaby: Boolean) {
        setMetadata("isBaby", isBaby)
    }

    fun isBaby(): Boolean {
        return getMetadata("isBaby")
    }

    fun isChargingCrossbow(): Boolean {
        return getMetadata("isChargingCrossbow")
    }

    fun setChargingCrossbow(isChargingCrossbow: Boolean) {
        setMetadata("isChargingCrossbow", isChargingCrossbow)
    }

    fun isDancing(): Boolean {
        return getMetadata("isDancing")
    }

    fun setDancing(isDancing: Boolean) {
        setMetadata("isDancing", isDancing)
    }
}