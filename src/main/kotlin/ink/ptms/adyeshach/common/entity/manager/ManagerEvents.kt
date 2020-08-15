package ink.ptms.adyeshach.common.entity.manager

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.util.Tasks
import ink.ptms.adyeshach.internal.mirror.Mirror
import io.izzel.taboolib.module.inject.TFunction
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.inject.TSchedule
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

/**
 * @Author sky
 * @Since 2020-08-14 22:10
 */
@TListener
private class ManagerEvents : Listener {

    @TSchedule
    fun init() {
        AdyeshachAPI.getEntityManager().onLoad()
        Bukkit.getOnlinePlayers().forEach {
            AdyeshachAPI.getEntityManager(it).onLoad()
        }
    }

    @TFunction.Cancel
    fun cancel() {
        onSavePublic()
        onSavePrivate()
    }

    @TSchedule(period = 1, async = true)
    fun onTickPublic() {
        Mirror.get("ManagerPublic:onTick", false).eval {
            AdyeshachAPI.getEntityManager().onTick()
        }
    }

    @TSchedule(period = 1, async = true)
    fun onTickPrivate() {
        Bukkit.getOnlinePlayers().forEach { player ->
            Mirror.get("ManagerPrivate:onTick", false).eval {
                AdyeshachAPI.getEntityManager(player).onTick()
            }
        }
    }

    @TSchedule(period = 1200, async = true)
    fun onSavePublic() {
        Mirror.get("ManagerPublic:onSave").eval {
            AdyeshachAPI.getEntityManager().onSave()
        }
    }

    @TSchedule(period = 600, async = true)
    fun onSavePrivate() {
        Bukkit.getOnlinePlayers().forEach {
            Mirror.get("ManagerPrivate:onSave").eval {
                AdyeshachAPI.getEntityManager(it).onSave()
            }
        }
    }

    @EventHandler
    fun e(e: PlayerJoinEvent) {
        AdyeshachAPI.getEntityManager().getEntities().filter { it.isPublic() }.forEach {
            it.viewPlayers.viewers.add(e.player.name)
        }
        Tasks.delay(100, true) {
            Mirror.get("ManagerPrivate:onLoad").eval {
                AdyeshachAPI.getEntityManager(e.player).onLoad()
            }
        }
    }

    @EventHandler
    fun e(e: PlayerQuitEvent) {
        AdyeshachAPI.getEntityManager().getEntities().forEach {
            it.viewPlayers.viewers.remove(e.player.name)
            it.viewPlayers.visible.remove(e.player.name)
        }
        Mirror.get("ManagerPrivate:onSave").eval {
            AdyeshachAPI.getEntityManager(e.player).onSave()
        }
    }
}