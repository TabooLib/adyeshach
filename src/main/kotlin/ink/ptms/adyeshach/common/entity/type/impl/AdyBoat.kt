package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.bukkit.BukkitBoat
import io.izzel.taboolib.Version
import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.entity.Player

/**
 * @author Arasple
 * @date 2020/8/4 22:48
 */
class AdyBoat(owner: Player) : AdyEntity(owner, EntityTypes.BOAT) {

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