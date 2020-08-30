package ink.ptms.adyeshach.common.entity.manager

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.event.AdyeshachPlayerJoinEvent
import ink.ptms.adyeshach.internal.mirror.Mirror
import io.izzel.taboolib.module.inject.TFunction
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.inject.TSchedule
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.spigotmc.AsyncCatcher

/**
 * @Author sky
 * @Since 2020-08-14 22:10
 */
@TListener
private class ManagerEvents : Listener {

    @TSchedule
    fun init() {
        Bukkit.getOnlinePlayers().forEach {
            AdyeshachAPI.getEntityManagerPrivate(it).onEnable()
        }
        AdyeshachAPI.getEntityManagerPublic().onEnable()
    }

    @TFunction.Cancel
    fun cancel() {
        Bukkit.getOnlinePlayers().forEach {
            AdyeshachAPI.getEntityManagerPrivate(it).onDisable()
        }
        AdyeshachAPI.getEntityManagerPublic().onDisable()
        onSavePublic()
        onSavePrivate()
    }

    @TSchedule(period = 1, async = true)
    fun onTickPublic() {
        val asyncCatcher = AsyncCatcher.enabled
        AsyncCatcher.enabled = false
        Mirror.get("ManagerPublic:onTick(async)", false).eval {
            AdyeshachAPI.getEntityManagerPublic().onTick()
        }
        Mirror.get("ManagerPublic:onTick(async)(temporary)", false).eval {
            AdyeshachAPI.getEntityManagerPublicTemporary().onTick()
        }
        AsyncCatcher.enabled = asyncCatcher
    }

    @TSchedule(period = 1, async = true)
    fun onTickPrivate() {
        val asyncCatcher = AsyncCatcher.enabled
        AsyncCatcher.enabled = false
        Bukkit.getOnlinePlayers().forEach { player ->
            Mirror.get("ManagerPrivate:onTick(async)", false).eval {
                AdyeshachAPI.getEntityManagerPrivate(player).onTick()
            }
            Mirror.get("ManagerPrivate:onTick(async)(temporary)", false).eval {
                AdyeshachAPI.getEntityManagerPrivateTemporary(player).onTick()
            }
        }
        AsyncCatcher.enabled = asyncCatcher
    }

    @TSchedule(period = 1200, async = true)
    fun onSavePublic() {
        Mirror.get("ManagerPublic:onSave(async)").eval {
            AdyeshachAPI.getEntityManagerPublic().onSave()
        }
    }

    @TSchedule(period = 600, async = true)
    fun onSavePrivate() {
        Bukkit.getOnlinePlayers().forEach {
            Mirror.get("ManagerPrivate:onSave(async)").eval {
                AdyeshachAPI.getEntityManagerPrivate(it).onSave()
            }
        }
    }

    @EventHandler
    fun e(e: AdyeshachPlayerJoinEvent) {
        AdyeshachAPI.getEntityManagerPublic().getEntities().filter { it.isPublic() }.forEach {
            it.viewPlayers.viewers.add(e.player.name)
        }
        AdyeshachAPI.getEntityManagerPublicTemporary().getEntities().filter { it.isPublic() }.forEach {
            it.viewPlayers.viewers.add(e.player.name)
        }
        Mirror.get("ManagerPrivate:onLoad(async)").eval {
            AdyeshachAPI.getEntityManagerPrivate(e.player).onEnable()
        }
    }

    @EventHandler
    fun e(e: PlayerQuitEvent) {
        AdyeshachAPI.getEntityManagerPublic().getEntities().forEach {
            it.viewPlayers.viewers.remove(e.player.name)
            it.viewPlayers.visible.remove(e.player.name)
        }
        AdyeshachAPI.getEntityManagerPublicTemporary().getEntities().forEach {
            it.viewPlayers.viewers.remove(e.player.name)
            it.viewPlayers.visible.remove(e.player.name)
        }
        Mirror.get("ManagerPrivate:onSave(async)").eval {
            AdyeshachAPI.getEntityManagerPrivate(e.player).onSave()
        }
    }
}