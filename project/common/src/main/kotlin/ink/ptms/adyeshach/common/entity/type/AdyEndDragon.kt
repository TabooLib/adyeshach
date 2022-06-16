package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitDragonPhase

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
abstract class AdyEndDragon : AdyMob() {

    open fun setDragonPhase(dragonPhase: BukkitDragonPhase) {
        setMetadata("dragonPhase", dragonPhase.ordinal)
    }

    open fun getDragonPhase(): BukkitDragonPhase {
        return BukkitDragonPhase.of(getMetadata("dragonPhase"))
    }
}