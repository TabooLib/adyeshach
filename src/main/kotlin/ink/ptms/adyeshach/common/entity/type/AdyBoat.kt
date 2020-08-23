package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitBoat
import ink.ptms.adyeshach.common.editor.Editors
import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author Arasple
 * @date 2020/8/4 22:48
 */
class AdyBoat() : AdyEntity(EntityTypes.BOAT) {

    /**
     * 1.13 -> Index 6-12, 且 leftPaddleTurning / rightPaddleTurning 换位置
     * 1.12 -> Index 6-11, 在 1.13 基础上砍掉最后一个
     * 1.9 ->  Index 5-10 和 1.12 对应
     */
    init {
        registerMeta(at(11600 to 7, 11000 to 6, 10900 to 5), "sinceLastHit", 0)
        registerMeta(at(11600 to 8, 11000 to 7, 10900 to 6), "forwardDirection", 1)
        registerMeta(at(11600 to 9, 11000 to 8, 10900 to 7), "damageTaken", 0f)
        registerMeta(at(11600 to 10, 11000 to 9, 10900 to 8), "type", BukkitBoat.OAK.ordinal)
                .from(Editors.enums(BukkitBoat::class) { _, entity, meta, index, _ -> "/adyeshachapi edit int ${entity.uniqueId} ${meta.key} $index" })
                .display { _, entity, _ ->
                    BukkitBoat.values()[entity.getMetadata("type")].name
                }.build()
        registerMeta(at(11600 to 11, 11000 to 11, 10900 to 10), "leftPaddleTurning", false)
        registerMeta(at(11600 to 12, 11000 to 10, 10900 to 9), "rightPaddleTurning", false)
        registerMeta(at(11600 to 13, 11300 to 12), "splashTimer", 0)
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