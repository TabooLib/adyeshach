package ink.ptms.adyeshach.impl.entity.manager

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.impl.DefaultAdyeshachAPI
import ink.ptms.adyeshach.impl.DefaultAdyeshachBooster
import org.bukkit.Bukkit
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.platform.util.bukkitPlugin
import taboolib.platform.util.onlinePlayers
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.manager.DefaultManagerHandler
 *
 * @author 坏黑
 * @since 2022/8/18 10:51
 */
internal object DefaultManagerHandler {

    val pool: ScheduledExecutorService = Executors.newScheduledThreadPool(16)

    @Awake(LifeCycle.ACTIVE)
    fun onActive() {
        // 公共管理器
        DefaultAdyeshachBooster.api.localPublicEntityManager.onEnable()
        // 私有管理器
        onlinePlayers.forEach { Adyeshach.api().setupEntityManager(it) }
        // 公共调度器
        Bukkit.getScheduler().runTaskTimer(bukkitPlugin, Runnable {
            DefaultAdyeshachBooster.api.localPublicEntityManager.onTick()
            DefaultAdyeshachBooster.api.localPublicEntityManagerTemporary.onTick()
        }, 1L, 1L)
        // 私有调度器
        Bukkit.getScheduler().runTaskTimer(bukkitPlugin, Runnable {
            onlinePlayers.forEach { player ->
                if (DefaultAdyeshachAPI.playerEntityManagerMap.containsKey(player.name)) {
                    DefaultAdyeshachAPI.playerEntityManagerMap[player.name]?.onTick()
                }
                if (DefaultAdyeshachAPI.playerEntityTemporaryManagerMap.containsKey(player.name)) {
                    DefaultAdyeshachAPI.playerEntityTemporaryManagerMap[player.name]?.onTick()
                }
            }
        }, 1L, 1L)
    }

    @Awake(LifeCycle.DISABLE)
    fun onDisable() {
        // 公共管理器
        DefaultAdyeshachBooster.api.localPublicEntityManagerTemporary.onDisable()
        DefaultAdyeshachBooster.api.localPublicEntityManager.onDisable()
        DefaultAdyeshachBooster.api.localPublicEntityManager.onSave()
        // 私有管理器
        onlinePlayers.forEach { Adyeshach.api().releaseEntityManager(it) }
    }
}