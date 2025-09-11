package ink.ptms.adyeshach.core.bukkit

import net.minecraft.world.entity.animal.armadillo.Armadillo

enum class BukkitArmadilloState(val state: Armadillo.a) {
    IDLE(Armadillo.a.IDLE),
    ROLLING(Armadillo.a.ROLLING),
    SCARED(Armadillo.a.SCARED),
    UNROLLING(Armadillo.a.UNROLLING)
}