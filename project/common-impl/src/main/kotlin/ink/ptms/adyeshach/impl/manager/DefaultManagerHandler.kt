package ink.ptms.adyeshach.impl.manager

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.impl.DefaultAdyeshachAPI
import ink.ptms.adyeshach.impl.DefaultAdyeshachBooster
import ink.ptms.adyeshach.impl.storage.EntityStorage
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.submitAsync
import taboolib.platform.util.onlinePlayers

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.manager.DefaultManagerHandler
 *
 * @author 坏黑
 * @since 2022/8/18 10:51
 */
internal object DefaultManagerHandler {

    // 当前游戏刻的玩家列表
    var playersInGameTick: Collection<Player> = listOf()

    @Awake(LifeCycle.ACTIVE)
    fun onActive() {
        // 公共管理器
        DefaultAdyeshachBooster.api.localPublicEntityManager.onEnable()
        // 私有管理器
        onlinePlayers.forEach { Adyeshach.api().setupEntityManager(it) }
        // 公共管理器
        submit(period = 1) {
            playersInGameTick = Bukkit.getOnlinePlayers()
            DefaultAdyeshachBooster.api.localPublicEntityManager.onTick()
            DefaultAdyeshachBooster.api.localPublicEntityManagerTemporary.onTick()
        }
        // 私有管理器
        submit(period = 1) {
            onlinePlayers.forEach { player ->
                if (DefaultAdyeshachAPI.playerEntityManagerMap.containsKey(player.name)) {
                    DefaultAdyeshachAPI.playerEntityManagerMap[player.name]?.onTick()
                }
                if (DefaultAdyeshachAPI.playerEntityTemporaryManagerMap.containsKey(player.name)) {
                    DefaultAdyeshachAPI.playerEntityTemporaryManagerMap[player.name]?.onTick()
                }
            }
        }
        // 自动保存
        submitAsync(period = 1200, delay = 1200) {
            // 公共管理器
            DefaultAdyeshachBooster.api.localPublicEntityManager.onSave()
            // 私有管理器
            if (EntityStorage.isEnabled()) {
                onlinePlayers.forEach { player ->
                    if (DefaultAdyeshachAPI.playerEntityManagerMap.containsKey(player.name)) {
                        DefaultAdyeshachAPI.playerEntityManagerMap[player.name]?.onSave()
                    }
                }
            }
        }
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