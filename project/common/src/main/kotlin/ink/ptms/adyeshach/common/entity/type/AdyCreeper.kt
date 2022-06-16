package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitCreeperState

/**
 * @author sky
 * @since 2020-08-04 19:30
 */
abstract class AdyCreeper : AdyMob() {

    open fun setState(value: BukkitCreeperState) {
        setMetadata("state", value.value)
    }

    open fun getState(): BukkitCreeperState {
        return BukkitCreeperState.of(getMetadata("state"))
    }

    open fun setCharged(value: Boolean) {
        setMetadata("isCharged", value)
    }

    open fun isCharged(): Boolean {
        return getMetadata("isCharged")
    }

    open fun setIgnited(value: Boolean) {
        setMetadata("isIgnited", value)
    }

    open fun isIgnited(): Boolean {
        return getMetadata("isIgnited")
    }
}