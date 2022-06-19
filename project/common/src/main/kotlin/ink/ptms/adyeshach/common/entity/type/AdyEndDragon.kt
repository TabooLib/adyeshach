package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitDragonPhase

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyEndDragon : AdyMob {

    fun setDragonPhase(dragonPhase: BukkitDragonPhase) {
        setMetadata("dragonPhase", dragonPhase.ordinal)
    }

    fun getDragonPhase(): BukkitDragonPhase {
        return BukkitDragonPhase.of(getMetadata("dragonPhase"))
    }
}