package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
open class AdyPiglin(entityTypes: EntityTypes) : AdyEntityLiving(entityTypes) {

    constructor(): this(EntityTypes.PIGLIN)

    init {
//        val index = at(11700 to 16, 11600 to 15)
//        natural(index + 0, "isImmuneToZombification", false)
//        natural(index + 1, "isBaby", false)
//        natural(index + 2, "isChargingCrossbow", false)
//        natural(index + 3, "isDancing", false)
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