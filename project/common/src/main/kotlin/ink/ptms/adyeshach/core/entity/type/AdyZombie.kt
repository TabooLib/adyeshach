package ink.ptms.adyeshach.core.entity.type

/**
 * @author sky
 * @since 2020-08-04 18:28
 */
interface AdyZombie : AdyMob {

    var isBecomingDrowned: Boolean
        get() = getMetadata("isBecomingDrowned")
        set(value) {
            setMetadata("isBecomingDrowned", value)
        }

    fun setBaby(value: Boolean) {
        setMetadata("isBaby", value)
    }

    fun isBaby(): Boolean {
        return getMetadata("isBaby")
    }

    fun setDrowning(value: Boolean) {
        setMetadata("isDrowning", value)
    }

    fun isDrowning(): Boolean {
        return getMetadata("isDrowning")
    }

    fun setHandsHeldUp(value: Boolean) {
        setMetadata("isHandsHeldUp", value)
    }

    fun getHandsHeldUp(): Boolean {
        return getMetadata("isHandsHeldUp")
    }

    fun setZombieType(value: Int) {
        setMetadata("zombieType", value)
    }

    fun getZombieType(): Int {
        return getMetadata("zombieType")
    }

    fun setConverting(value: Boolean) {
        setMetadata("isConverting", value)
    }

    fun isConverting(): Boolean {
        return getMetadata("isConverting")
    }
}