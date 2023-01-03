package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitDragonPhase
import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
class AdyEndDragon(v2: ink.ptms.adyeshach.core.entity.EntityInstance):  AdyMob(EntityTypes.ENDER_DRAGON, v2) {

    fun setDragonPhase(dragonPhase: BukkitDragonPhase) {
        setMetadata("dragonPhase", dragonPhase.ordinal)
    }

    fun getDragonPhase(): BukkitDragonPhase {
        return BukkitDragonPhase.of(getMetadata("dragonPhase"))
    }
}