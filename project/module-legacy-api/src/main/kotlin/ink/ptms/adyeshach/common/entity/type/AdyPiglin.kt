package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
open class AdyPiglin(entityTypes: EntityTypes, v2: ink.ptms.adyeshach.core.entity.EntityInstance) : AdyEntityLiving(entityTypes, v2) {

    constructor(v2: ink.ptms.adyeshach.core.entity.EntityInstance): this(EntityTypes.PIGLIN, v2)

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