package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitCreeperState

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @since 2020-08-04 19:30
 */
@Deprecated("Outdated but usable")
class AdyCreeper(v2: ink.ptms.adyeshach.core.entity.EntityInstance) : AdyMob(EntityTypes.CREEPER, v2) {

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