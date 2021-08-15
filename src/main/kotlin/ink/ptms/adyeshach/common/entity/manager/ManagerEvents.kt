package ink.ptms.adyeshach.common.entity.manager

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.AdyeshachAPI.toDistance
import ink.ptms.adyeshach.api.AdyeshachSettings
import ink.ptms.adyeshach.api.event.AdyeshachEntitySpawnEvent
import ink.ptms.adyeshach.api.event.AdyeshachPlayerJoinEvent
import ink.ptms.adyeshach.common.script.ScriptHandler
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.*
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.warning
import taboolib.common5.mirrorNow

/**
 * @Author sky
 * @Since 2020-08-14 22:10
 */
internal object ManagerEvents {

    @Awake(LifeCycle.ACTIVE)
    fun init() {
        Bukkit.getOnlinePlayers().forEach {
            AdyeshachAPI.getEntityManagerPrivate(it).onEnable()
        }
        AdyeshachAPI.getEntityManagerPublic().onEnable()
        try {
            ScriptHandler.workspace.loadAll()
        } catch (e: Exception) {
            warning("An error occurred while loading the script")
            e.printStackTrace()
        }
    }

    @Awake(LifeCycle.DISABLE)
    fun cancel() {
        Bukkit.getOnlinePlayers().forEach {
            val manager = AdyeshachAPI.getEntityManagerPrivate(it)
            manager.onDisable()
            manager.onSave()
        }
        val manager = AdyeshachAPI.getEntityManagerPublic()
        manager.onDisable()
        manager.onSave()
    }

    @Awake(LifeCycle.ACTIVE)
    fun onTick() {
        submit(period = 1) {
            mirrorNow("ManagerPublic:onTick") {
                AdyeshachAPI.getEntityManagerPublic().onTick()
            }
            mirrorNow("ManagerPublic:onTick(temporary)") {
                AdyeshachAPI.getEntityManagerPublicTemporary().onTick()
            }
        }
        submit(period = 1) {
            Bukkit.getOnlinePlayers().forEach { player ->
                mirrorNow("ManagerPrivate:onTick") {
                    AdyeshachAPI.getEntityManagerPrivate(player).onTick()
                }
                mirrorNow("ManagerPrivate:onTick(temporary)") {
                    AdyeshachAPI.getEntityManagerPrivateTemporary(player).onTick()
                }
            }
        }
    }

    @Awake(LifeCycle.ACTIVE)
    fun onSave() {
        submit(delay = 1200, period = 1200, async = true) {
            mirrorNow("ManagerPublic:onSave(async)") {
                AdyeshachAPI.getEntityManagerPublic().onSave()
            }
        }
        submit(delay = 600, period = 600, async = true) {
            Bukkit.getOnlinePlayers().forEach {
                mirrorNow("ManagerPrivate:onSave(async)") {
                    AdyeshachAPI.getEntityManagerPrivate(it).onSave()
                }
            }
        }
    }

    @SubscribeEvent
    fun e(e: PlayerJoinEvent) {
        if (AdyeshachSettings.spawnTrigger == AdyeshachSettings.SpawnTrigger.JOIN) {
            submit(delay = 20) { spawn(e.player) }
        }
    }

    @SubscribeEvent
    fun e(e: AdyeshachPlayerJoinEvent) {
        if (AdyeshachSettings.spawnTrigger == AdyeshachSettings.SpawnTrigger.KEEP_ALIVE) {
            spawn(e.player)
        }
    }

    @SubscribeEvent
    fun e(e: PlayerRespawnEvent) {
        submit(delay = 20) {
            AdyeshachAPI.getEntities(e.player) { it.position.toLocation().toDistance(e.player.location) < 128 }.forEach {
                it.visible(e.player, true)
                AdyeshachEntitySpawnEvent(it).call()
            }
        }
    }

    @SubscribeEvent
    fun e(e: PlayerQuitEvent) {
        AdyeshachAPI.getEntityManagerPublic().getEntities().forEach {
            it.viewPlayers.viewers.remove(e.player.name)
            it.viewPlayers.visible.remove(e.player.name)
        }
        AdyeshachAPI.getEntityManagerPublicTemporary().getEntities().forEach {
            it.viewPlayers.viewers.remove(e.player.name)
            it.viewPlayers.visible.remove(e.player.name)
        }
        mirrorNow("ManagerPrivate:onSave(async)") {
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
        mirrorNow("ManagerPrivate:onLoad(async)") {
            AdyeshachAPI.getEntityManagerPrivate(player).onEnable()
        }
    }
}