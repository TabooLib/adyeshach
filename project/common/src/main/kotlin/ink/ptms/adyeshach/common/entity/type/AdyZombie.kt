package ink.ptms.adyeshach.common.entity.type

/**
 * @author sky
 * @since 2020-08-04 18:28
 */
abstract class AdyZombie : AdyMob() {

    var isBecomingDrowned: Boolean
        get() = getMetadata("isBecomingDrowned")
        set(value) {
            setMetadata("isBecomingDrowned", value)
        }

    open fun setBaby(value: Boolean) {
        setMetadata("isBaby", value)
    }

    open fun isBaby(): Boolean {
        return getMetadata("isBaby")
    }

    open fun setDrowning(value: Boolean) {
        setMetadata("isDrowning", value)
    }

    open fun isDrowning(): Boolean {
        return getMetadata("isDrowning")
    }

    open fun setHandsHeldUp(value: Boolean) {
        setMetadata("isHandsHeldUp", value)
    }

    open fun getHandsHeldUp(): Boolean {
        return getMetadata("isHandsHeldUp")
    }

    open fun setZombieType(value: Int) {
        setMetadata("zombieType", value)
    }

    open fun getZombieType(): Int {
        return getMetadata("zombieType")
    }

    open fun setConverting(value: Boolean) {
        setMetadata("isConverting", value)
    }

    open fun isConverting(): Boolean {
        return getMetadata("isConverting")
    }
}