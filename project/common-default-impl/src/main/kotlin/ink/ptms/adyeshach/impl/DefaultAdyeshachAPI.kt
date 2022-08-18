package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.common.api.*
import ink.ptms.adyeshach.common.util.safeDistance
import ink.ptms.adyeshach.impl.entity.manager.DefaultManager
import ink.ptms.adyeshach.impl.entity.manager.DefaultPlayerManager
import org.bukkit.entity.Player
import taboolib.platform.util.setMeta
import java.util.concurrent.ConcurrentHashMap

/**
 * Adyeshach
 * ink.ptms.adyeshach.internal.nms.DefaultAdyeshachAPI
 *
 * @author 坏黑
 * @since 2022/6/18 16:20
 */
class DefaultAdyeshachAPI : AdyeshachAPI {

    var entityFinder = DefaultAdyeshachEntityFinder()
    var entitySerializer = DefaultAdyeshachEntitySerializer()
    var entityTypeHelper = DefaultAdyeshachEntityTypeHandler()
    var entityMetadataHandler = DefaultAdyeshachEntityMetadataHandler()
    var entityControllerHandler = DefaultAdyeshachEntityControllerHandler()
    var hologramHandler = DefaultAdyeshachHologramHandler()
    var ketherHandler = DefaultAdyeshachKetherHandler()
    var minecraftAPI = DefaultAdyeshachMinecraftAPI()
    var networkAPI = DefaultAdyeshachNetworkAPI()
    var language = DefaultAdyeshachLanguage()

    var publicEntityManager = DefaultManager()
    var publicEntityManagerTemp = DefaultManager()

    override fun setupEntityManager(player: Player) {
        if (player.isOnline && !player.hasMetadata("adyeshach_setup")) {
            // 设置标签避免重复执行
            player.setMeta("adyeshach_setup", true)
            // 公共管理器
            getPublicEntityManager().getEntities { it.visibleAfterLoaded }.forEach {
                it.viewPlayers.viewers += player.name
            }
            getPublicEntityManager(true).getEntities { it.visibleAfterLoaded }.forEach {
                it.viewPlayers.viewers += player.name
            }
            // 私有管理器
            getPrivateEntityManager(player).onEnable()
        }
    }

    override fun releaseEntityManager(player: Player) {
        if (player.hasMetadata("adyeshach_setup")) {
            // 公共管理器
            getPublicEntityManager().getEntities { it.visibleAfterLoaded }.forEach {
                it.removeViewer(player)
            }
            getPublicEntityManager(true).getEntities { it.visibleAfterLoaded }.forEach {
                it.removeViewer(player)
            }
            // 私有管理器
            var privateManager = getPrivateEntityManager(player)
            privateManager.onDisable()
            privateManager.onSave()
            privateManager = getPrivateEntityManager(player, true)
            privateManager.onDisable()
            // 移除管理器
            playerEntityManagerMap.remove(player.name)
            playerEntityTemporaryManagerMap.remove(player.name)
        }
    }

    override fun refreshEntityManager(player: Player) {
        // 对范围内可视且在观察者列表的实体进行刷新
        entityFinder.getEntities(player) { it.isViewer(player) && it.getLocation().safeDistance(player.location) < it.visibleDistance }.forEach {
            it.visible(player, true)
        }
    }

    override fun getPublicEntityManager(temporary: Boolean): DefaultManager {
        return if (temporary) publicEntityManagerTemp else publicEntityManager
    }

    override fun getPrivateEntityManager(player: Player, temporary: Boolean): DefaultPlayerManager {
        val map = if (temporary) playerEntityTemporaryManagerMap else playerEntityManagerMap
        return if (map.containsKey(player.name)) {
            map[player.name]!!
        } else {
            val manager = DefaultPlayerManager(player)
            map[player.name] = manager
            manager
        }
    }

    override fun getEntityFinder(): AdyeshachEntityFinder {
        return entityFinder
    }

    override fun getEntitySerializer(): AdyeshachEntitySerializer {
        return entitySerializer
    }

    override fun getEntityTypeHandler(): AdyeshachEntityTypeHandler {
        return entityTypeHelper
    }

    override fun getEntityMetadataHandler(): AdyeshachEntityMetadataHandler {
        return entityMetadataHandler
    }

    override fun getEntityControllerHandler(): AdyeshachEntityControllerHandler {
        return entityControllerHandler
    }

    override fun getHologramHandler(): AdyeshachHologramHandler {
        return hologramHandler
    }

    override fun getKetherHandler(): AdyeshachKetherHandler {
        return ketherHandler
    }

    override fun getMinecraftAPI(): AdyeshachMinecraftAPI {
        return minecraftAPI
    }

    override fun getNetworkAPI(): AdyeshachNetworkAPI {
        return networkAPI
    }

    override fun getLanguage(): AdyeshachLanguage {
        return language
    }

    companion object {

        val playerEntityManagerMap = ConcurrentHashMap<String, DefaultPlayerManager>()
        val playerEntityTemporaryManagerMap = ConcurrentHashMap<String, DefaultPlayerManager>()
    }
}