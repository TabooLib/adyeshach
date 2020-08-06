package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.api.nms.NMS
import org.bukkit.entity.Player
import java.lang.RuntimeException

/**
 * @Author sky
 * @Since 2020-08-04 18:28
 */
open class AdyZombie(owner: Player, entityTypes: EntityTypes) : AdyEntityAgeable(owner, entityTypes) {

    init {
        /**
         * 1.14 -> 16 isDrowning
         * 1.13 -> 15 isDrowning, 14 Are hands held up
         * 1.12 -> 14 Are hands held up
         * 1.10 -> 14 isConverting 15 Are hands held up
         * 1.9 -> 13+
         *
         * 1.9, 1.10 -> no zombie villager
         */
        registerMeta(at(11500 to 17, 11400 to 16, 11300 to 15), "isDrowning", false)
        registerMeta(at(11500 to 18, 11400 to 17, 11300 to 16, 11100 to 15, 11000 to 14, 10900 to 13), "isConverting", false)
        registerMeta(at(11400 to -1, 11200 to 14, 11000 to 15, 10900 to 14), "isHandsHeldUp", false)
        registerMeta(at(11100 to -1, 11000 to 13, 10900 to 12), "zombieType", 0)
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

    open fun setConverting(value: Boolean) {
        return setMetadata("isConverting", value)
    }

    open fun isConverting(): Boolean {
        return getMetadata("isConverting")
    }
}