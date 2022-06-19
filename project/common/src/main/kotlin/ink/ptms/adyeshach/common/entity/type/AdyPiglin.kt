package ink.ptms.adyeshach.common.entity.type

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyPiglin : AdyEntityLiving {

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