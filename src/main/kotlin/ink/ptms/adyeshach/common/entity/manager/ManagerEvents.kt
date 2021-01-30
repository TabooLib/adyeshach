package ink.ptms.adyeshach.common.entity.manager

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.Settings
import ink.ptms.adyeshach.api.event.AdyeshachPlayerJoinEvent
import ink.ptms.adyeshach.common.util.Tasks
import ink.ptms.adyeshach.internal.mirror.Mirror
import io.izzel.taboolib.module.inject.TFunction
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.inject.TSchedule
import org.bukkit.Bukkit
import org.bukkit.entity.Player
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

    @TSchedule(period = 1)
    fun onTickPublic() {
        Mirror.get("ManagerPublic:onTick").check {
            AdyeshachAPI.getEntityManagerPublic().onTick()
        }
        Mirror.get("ManagerPublic:onTick(temporary)").check {
            AdyeshachAPI.getEntityManagerPublicTemporary().onTick()
        }
    }

    @TSchedule(period = 1)
    fun onTickPrivate() {
        Bukkit.getOnlinePlayers().forEach { player ->
            Mirror.get("ManagerPrivate:onTick").check {
                AdyeshachAPI.getEntityManagerPrivate(player).onTick()
            }
            Mirror.get("ManagerPrivate:onTick(temporary)").check {
                AdyeshachAPI.getEntityManagerPrivateTemporary(player).onTick()
            }
        }
    }

    @TSchedule(period = 1200, async = true)
    fun onSavePublic() {
        Mirror.get("ManagerPublic:onSave(async)").check {
            AdyeshachAPI.getEntityManagerPublic().onSave()
        }
    }

    @TSchedule(period = 600, async = true)
    fun onSavePrivate() {
        Bukkit.getOnlinePlayers().forEach {
            Mirror.get("ManagerPrivate:onSave(async)").check {
                AdyeshachAPI.getEntityManagerPrivate(it).onSave()
            }
        }
    }

    @EventHandler
    fun e(e: PlayerJoinEvent) {
        if (Settings.get().spawnTrigger == Settings.SpawnTrigger.JOIN) {
           Tasks.delay(20) {
               spawn(e.player)
           }
        }
    }

    @EventHandler
    fun e(e: AdyeshachPlayerJoinEvent) {
        if (Settings.get().spawnTrigger == Settings.SpawnTrigger.KEEP_ALIVE) {
            spawn(e.player)
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
        Mirror.get("ManagerPrivate:onSave(async)").check {
            AdyeshachAPI.getEntityManagerPrivate(e.player).onSave()
        }
    }

    fun spawn(player: Player) {
        AdyeshachAPI.getEntityManagerPublic().getEntities().filter { it.isPublic() && it.alwaysVisible }.forEach {
            it.viewPlayers.viewers.add(player.name)
        }
        AdyeshachAPI.getEntityManagerPublicTemporary().getEntities().filter { it.isPublic() && it.alwaysVisible }.forEach {
            it.viewPlayers.viewers.add(player.name)
        }
        Mirror.get("ManagerPrivate:onLoad(async)").check {
            AdyeshachAPI.getEntityManagerPrivate(player).onEnable()
        }
    }
}