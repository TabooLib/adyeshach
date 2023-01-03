package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitBoat
import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * 1.13 -> Index 6-12, 且 leftPaddleTurning / rightPaddleTurning 换位置
 * 1.12 -> Index 6-11, 在 1.13 基础上砍掉最后一个
 * 1.9 ->  Index 5-10 和 1.12 对应
 *
 * @author Arasple
 * @date 2020/8/4 22:48
 */
@Deprecated("Outdated but usable")
class AdyBoat(v2: ink.ptms.adyeshach.core.entity.EntityInstance) : AdyEntity(EntityTypes.BOAT, v2) {

    fun setTimeSinceLastHit(time: Int) {
        setMetadata("sinceLastHit", time)
    }

    fun getTimeSinceLastHit(): Int {
        return getMetadata("sinceLastHit")
    }

    fun setForwardDirection(direction: Int) {
        setMetadata("forwardDirection", direction)
    }

    fun getForwardDirection(): Int {
        return getMetadata("forwardDirection")
    }

    fun setDamageTaken(damageTaken: Float) {
        setMetadata("damageTaken", damageTaken)
    }

    fun getDamageTaken(): Float {
        return getMetadata("damageTaken")
    }

    fun setType(type: BukkitBoat) {
        setMetadata("type", type.ordinal)
    }

    fun getType(): BukkitBoat {
        return BukkitBoat.of(getMetadata("type"))
    }

    fun setLeftPaddleTurning(boolean: Boolean) {
        setMetadata("leftPaddleTurning", boolean)
    }

    fun isLeftPaddleTurning(): Boolean {
        return getMetadata("leftPaddleTurning")
    }

    fun setRightPaddleTurning(boolean: Boolean) {
        setMetadata("rightPaddleTurning", boolean)
    }

    fun isRightPaddleTurning(): Boolean {
        return getMetadata("rightPaddleTurning")
    }

    fun setSplashTimer(timer: Int) {
        setMetadata("splashTimer", timer)
    }

    fun getSplashTimer(): Int {
        return getMetadata("splashTimer")
    }
}