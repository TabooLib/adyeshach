package ink.ptms.adyeshach.common.entity.manager

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.AdyeshachSettings
import ink.ptms.adyeshach.api.event.AdyeshachPlayerJoinEvent
import ink.ptms.adyeshach.common.script.ScriptHandler
import ink.ptms.adyeshach.common.util.safeDistance
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.warning

/**
 * @author sky
 * @since 2020-08-14 22:10
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
        AdyeshachAPI.getEntityManagerPublic().onDisable()
        AdyeshachAPI.getEntityManagerPublic().onSave()
    }

    @Awake(LifeCycle.ACTIVE)
    fun onTick() {
        submit(period = 1) {
            AdyeshachAPI.getEntityManagerPublic().onTick()
            AdyeshachAPI.getEntityManagerPublicTemporary().onTick()
        }
        submit(period = 1) {
            Bukkit.getOnlinePlayers().forEach { player ->
                AdyeshachAPI.getEntityManagerPrivate(player).onTick()
                AdyeshachAPI.getEntityManagerPrivateTemporary(player).onTick()
            }
        }
    }

    @Awake(LifeCycle.ACTIVE)
    fun onSave() {
        submit(delay = 1200, period = 1200, async = true) {
            AdyeshachAPI.getEntityManagerPublic().onSave()
        }
        submit(delay = 600, period = 600, async = true) {
            Bukkit.getOnlinePlayers().forEach {
                AdyeshachAPI.getEntityManagerPrivate(it).onSave()
            }
        }
    }

    @SubscribeEvent
    private fun onJoin(e: PlayerJoinEvent) {
        if (AdyeshachSettings.spawnTrigger == AdyeshachSettings.SpawnTrigger.JOIN) {
            submit(delay = 20) { spawn(e.player) }
        }
    }

    @SubscribeEvent
    private fun onJoin(e: AdyeshachPlayerJoinEvent) {
        if (AdyeshachSettings.spawnTrigger == AdyeshachSettings.SpawnTrigger.KEEP_ALIVE) {
            spawn(e.player)
        }
    }

    @SubscribeEvent
    private fun onRespawn(e: PlayerRespawnEvent) {
        submit(delay = 20) {
            AdyeshachAPI.getVisibleEntities(e.player).forEach {
                if (it.isViewer(e.player)) {
                    it.visible(e.player, false)
                    it.visible(e.player, true)
                }
            }
        }
    }

    @SubscribeEvent
    private fun onQuit(e: PlayerQuitEvent) {
        AdyeshachAPI.getEntityManagerPublic().getEntities().forEach {
            it.viewPlayers.viewers.remove(e.player.name)
            it.viewPlayers.visible.remove(e.player.name)
        }
        AdyeshachAPI.getEntityManagerPublicTemporary().getEntities().forEach {
            it.viewPlayers.viewers.remove(e.player.name)
            it.viewPlayers.visible.remove(e.player.name)
        }
        AdyeshachAPI.getEntityManagerPrivate(e.player).onSave()
    }

    fun spawn(player: Player) {
        if (player.isOnline) {
            AdyeshachAPI.getEntityManagerPublic().getEntities().filter { it.visibleAfterLoaded }.forEach {
                it.viewPlayers.viewers.add(player.name)
            }
            AdyeshachAPI.getEntityManagerPublicTemporary().getEntities().filter { it.visibleAfterLoaded }.forEach {
                it.viewPlayers.viewers.add(player.name)
            }
            AdyeshachAPI.getEntityManagerPrivate(player).onEnable()
        }
    }
}