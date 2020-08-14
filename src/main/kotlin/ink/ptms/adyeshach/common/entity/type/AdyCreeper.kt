package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitCreeperState
import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @Author sky
 * @Since 2020-08-04 19:30
 */
class AdyCreeper() : AdyEntityLiving(EntityTypes.CREEPER) {

    init {
        /**
         * 1.15 -> 15+
         * 1.14 -> 14+
         * 1.10 -> 12,13,14
         * 1.9 -> 11,12,13
         */
        registerMeta(at(11500.to(15), 11400.to(14), 11000.to(12), 10900.to(11)), "state", BukkitCreeperState.IDLE.ordinal)
        registerMeta(at(11500.to(16), 11400.to(15), 11000.to(13), 10900.to(12)), "isCharged", false)
        registerMeta(at(11500.to(17), 11400.to(16), 11000.to(14), 10900.to(13)), "isIgnited", false)
    }

    fun setState(value: BukkitCreeperState) {
        setMetadata("state", value.value)
    }

    fun getState(): BukkitCreeperState {
        return BukkitCreeperState.of(getMetadata("state"))
    }

    fun setCharged(value: Boolean) {
        setMetadata("isCharged", value)
    }

    fun isCharged(): Boolean {
        return getMetadata("isCharged")
    }

    fun setIgnited(value: Boolean) {
        setMetadata("isIgnited", value)
    }

    fun isIgnited(): Boolean {
        return getMetadata("isIgnited")
    }
}