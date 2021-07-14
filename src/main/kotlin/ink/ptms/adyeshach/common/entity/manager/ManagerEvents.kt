package ink.ptms.adyeshach.common.entity.manager

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.AdyeshachAPI.mirrorFuture
import ink.ptms.adyeshach.api.AdyeshachAPI.toDistance
import ink.ptms.adyeshach.api.Settings
import ink.ptms.adyeshach.api.event.AdyeshachEntitySpawnEvent
import ink.ptms.adyeshach.api.event.AdyeshachPlayerJoinEvent
import ink.ptms.adyeshach.common.script.ScriptHandler
import ink.ptms.adyeshach.common.util.Tasks
import io.izzel.taboolib.module.inject.TFunction
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.inject.TSchedule
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent

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
        try {
            ScriptHandler.workspace.loadAll()
        } catch (e: Exception) {
            println("[Adyeshach] An error occurred while loading the script")
            e.printStackTrace()
        }
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
        mirrorFuture("ManagerPublic:onTick") {
            AdyeshachAPI.getEntityManagerPublic().onTick()
            finish()
        }
        mirrorFuture("ManagerPublic:onTick(temporary)") {
            AdyeshachAPI.getEntityManagerPublicTemporary().onTick()
            finish()
        }
    }

    @TSchedule(period = 1)
    fun onTickPrivate() {
        Bukkit.getOnlinePlayers().forEach { player ->
            mirrorFuture("ManagerPrivate:onTick") {
                AdyeshachAPI.getEntityManagerPrivate(player).onTick()
                finish()
            }
            mirrorFuture("ManagerPrivate:onTick(temporary)") {
                AdyeshachAPI.getEntityManagerPrivateTemporary(player).onTick()
                finish()
            }
        }
    }

    @TSchedule(period = 1200, async = true)
    fun onSavePublic() {
        mirrorFuture("ManagerPublic:onSave(async)") {
            AdyeshachAPI.getEntityManagerPublic().onSave()
            finish()
        }
    }

    @TSchedule(period = 600, async = true)
    fun onSavePrivate() {
        Bukkit.getOnlinePlayers().forEach {
            mirrorFuture("ManagerPrivate:onSave(async)") {
                AdyeshachAPI.getEntityManagerPrivate(it).onSave()
                finish()
            }
        }
    }

    @EventHandler
    fun e(e: PlayerJoinEvent) {
        if (Settings.spawnTrigger == Settings.SpawnTrigger.JOIN) {
           Tasks.delay(20) {
               spawn(e.player)
           }
        }
    }

    @EventHandler
    fun e(e: AdyeshachPlayerJoinEvent) {
        if (Settings.spawnTrigger == Settings.SpawnTrigger.KEEP_ALIVE) {
            spawn(e.player)
        }
    }

    @EventHandler
    fun e(e: PlayerRespawnEvent) {
        Tasks.delay(20) {
            AdyeshachAPI.getEntities(e.player) { it.position.toLocation().toDistance(e.player.location) < 128 }.forEach {
                it.visible(e.player, true)
                AdyeshachEntitySpawnEvent(it).call()
            }
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
        mirrorFuture("ManagerPrivate:onSave(async)") {
            AdyeshachAPI.getEntityManagerPrivate(e.player).onSave()
            finish()
        }
    }

    fun spawn(player: Player) {
        AdyeshachAPI.getEntityManagerPublic().getEntities().filter { it.isPublic() && it.alwaysVisible }.forEach {
            it.viewPlayers.viewers.add(player.name)
        }
        AdyeshachAPI.getEntityManagerPublicTemporary().getEntities().filter { it.isPublic() && it.alwaysVisible }.forEach {
            it.viewPlayers.viewers.add(player.name)
        }
        mirrorFuture("ManagerPrivate:onLoad(async)") {
            AdyeshachAPI.getEntityManagerPrivate(player).onEnable()
            finish()
        }
    }
}