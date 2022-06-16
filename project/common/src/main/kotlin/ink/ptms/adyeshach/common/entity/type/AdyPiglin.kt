package ink.ptms.adyeshach.common.entity.type

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Suppress("SpellCheckingInspection")
abstract class AdyPiglin : AdyEntityLiving() {

    open fun setImmuneToZombification(isImmuneToZombification: Boolean) {
        setMetadata("isImmuneToZombification", isImmuneToZombification)
    }

    open fun isImmuneToZombification(): Boolean {
        return getMetadata("isImmuneToZombification")
    }

    open fun setBaby(isBaby: Boolean) {
        setMetadata("isBaby", isBaby)
    }

    open fun isBaby(): Boolean {
        return getMetadata("isBaby")
    }

    open fun isChargingCrossbow(): Boolean {
        return getMetadata("isChargingCrossbow")
    }

    open fun setChargingCrossbow(isChargingCrossbow: Boolean) {
        setMetadata("isChargingCrossbow", isChargingCrossbow)
    }

    open fun isDancing(): Boolean {
        return getMetadata("isDancing")
    }

    open fun setDancing(isDancing: Boolean) {
        setMetadata("isDancing", isDancing)
    }
}