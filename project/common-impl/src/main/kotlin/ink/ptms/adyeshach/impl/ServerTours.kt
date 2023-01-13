package ink.ptms.adyeshach.impl

import com.melluh.servertours.ServerTours
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.function.warning
import taboolib.common.util.Version
import taboolib.common.util.unsafeLazy
import tech.mistermel.servertours.api.ServerToursAPI

object ServerTours {

    val serverToursHooked by unsafeLazy { Bukkit.getPluginManager().getPlugin("ServerTours") != null }
    val serverToursVersion by unsafeLazy { Version(Bukkit.getPluginManager().getPlugin("ServerTours")?.description?.version ?: "0.0.0") }

    fun isRoutePlaying(player: Player): Boolean {
        if (!serverToursHooked) {
            return false
        }
        return when (serverToursVersion.version[0]) {
            1 -> ServerToursAPI.getInstance().isRoutePlaying(player)
            2 -> ServerTours.getInstance().playbackManager.getTouringPlayer(player) != null
            else -> {
                warning("ServerTours $serverToursVersion is not supported yet.")
                false
            }
        }
    }
}