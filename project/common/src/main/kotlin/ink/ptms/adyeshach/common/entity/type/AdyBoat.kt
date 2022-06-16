package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitBoat

/**
 * @author Arasple
 * @date 2020/8/4 22:48
 */
abstract class AdyBoat : AdyEntity() {

    open fun setTimeSinceLastHit(time: Int) {
        setMetadata("sinceLastHit", time)
    }

    open fun getTimeSinceLastHit(): Int {
        return getMetadata("sinceLastHit")
    }

    open fun setForwardDirection(direction: Int) {
        setMetadata("forwardDirection", direction)
    }

    open fun getForwardDirection(): Int {
        return getMetadata("forwardDirection")
    }

    open fun setDamageTaken(damageTaken: Float) {
        setMetadata("damageTaken", damageTaken)
    }

    open fun getDamageTaken(): Float {
        return getMetadata("damageTaken")
    }

    open fun setType(type: BukkitBoat) {
        setMetadata("type", type.ordinal)
    }

    open fun getType(): BukkitBoat {
        return BukkitBoat.of(getMetadata("type"))
    }

    open fun setLeftPaddleTurning(boolean: Boolean) {
        setMetadata("leftPaddleTurning", boolean)
    }

    open fun isLeftPaddleTurning(): Boolean {
        return getMetadata("leftPaddleTurning")
    }

    open fun setRightPaddleTurning(boolean: Boolean) {
        setMetadata("rightPaddleTurning", boolean)
    }

    open fun isRightPaddleTurning(): Boolean {
        return getMetadata("rightPaddleTurning")
    }

    open fun setSplashTimer(timer: Int) {
        setMetadata("splashTimer", timer)
    }

    open fun getSplashTimer(): Int {
        return getMetadata("splashTimer")
    }
}