package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.bukkit.BukkitBoat
import ink.ptms.adyeshach.common.entity.type.EntityTypes
import org.bukkit.TreeSpecies
import org.bukkit.entity.Boat
import org.bukkit.entity.Player

/**
 * @author Arasple
 * @date 2020/8/4 22:48
 */
class AdyBoat(owner: Player) : AdyEntity(owner, EntityTypes.BOAT) {

    /**
     * 1.13 -> Index 6-12, 且 leftPaddleTurning / rightPaddleTurning 换位置
     * 1.12 -> Index 6-11, 在 1.13 基础上砍掉最后一个
     * 1.9 ->  Index 5-10 和 1.12 对应
     */
    init {
        registerMeta(7, "sinceLastHit", 0)
        registerMeta(8, "forwardDirection", 1)
        registerMeta(9, "damageTaken", 0f)
        registerMeta(10, "type", BukkitBoat.OAK.ordinal)
        registerMeta(11, "leftPaddleTurning", false)
        registerMeta(12, "rightPaddleTurning", false)
        registerMeta(13, "splashTimer", 0)
    }

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