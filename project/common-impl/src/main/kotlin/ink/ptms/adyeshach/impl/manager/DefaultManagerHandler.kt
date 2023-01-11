package ink.ptms.adyeshach.impl.manager

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.impl.DefaultAdyeshachAPI
import ink.ptms.adyeshach.impl.DefaultAdyeshachBooster
import org.bukkit.Bukkit
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.platform.util.bukkitPlugin
import taboolib.platform.util.onlinePlayers

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.manager.DefaultManagerHandler
 *
 * @author 坏黑
 * @since 2022/8/18 10:51
 */
internal object DefaultManagerHandler {

    @Awake(LifeCycle.ACTIVE)
    fun onActive() {
        // 公共管理器
        DefaultAdyeshachBooster.api.localPublicEntityManager.onEnable()
        // 私有管理器
        onlinePlayers.forEach { Adyeshach.api().setupEntityManager(it) }
        // 公共管理器
        Bukkit.getScheduler().runTaskTimer(bukkitPlugin, Runnable {
            DefaultAdyeshachBooster.api.localPublicEntityManager.onTick()
            DefaultAdyeshachBooster.api.localPublicEntityManagerTemporary.onTick()
        }, 1, 1)
        // 私有管理器
        Bukkit.getScheduler().runTaskTimer(bukkitPlugin, Runnable {
            onlinePlayers.forEach { player ->
                if (DefaultAdyeshachAPI.playerEntityManagerMap.containsKey(player.name)) {
                    DefaultAdyeshachAPI.playerEntityManagerMap[player.name]?.onTick()
                }
                if (DefaultAdyeshachAPI.playerEntityTemporaryManagerMap.containsKey(player.name)) {
                    DefaultAdyeshachAPI.playerEntityTemporaryManagerMap[player.name]?.onTick()
                }
            }
        }, 1, 1)
        // 自动保存
        Bukkit.getScheduler().runTaskTimerAsynchronously(bukkitPlugin, Runnable {
            // 公共管理器
            DefaultAdyeshachBooster.api.localPublicEntityManager.onSave()
            // 私有管理器
            onlinePlayers.forEach { player ->
                if (DefaultAdyeshachAPI.playerEntityManagerMap.containsKey(player.name)) {
                    DefaultAdyeshachAPI.playerEntityManagerMap[player.name]?.onSave()
                }
            }
        }, 1200, 1200)
    }

    @Awake(LifeCycle.DISABLE)
    fun onDisable() {
        // 公共管理器
        DefaultAdyeshachBooster.api.localPublicEntityManagerTemporary.onDisable()
        DefaultAdyeshachBooster.api.localPublicEntityManager.onDisable()
        DefaultAdyeshachBooster.api.localPublicEntityManager.onSave()
        // 私有管理器
        onlinePlayers.forEach { Adyeshach.api().releaseEntityManager(it, false) }
    }
}