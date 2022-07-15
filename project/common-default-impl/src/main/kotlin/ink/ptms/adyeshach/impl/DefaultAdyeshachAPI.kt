package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.common.api.*
import ink.ptms.adyeshach.common.entity.manager.Manager
import ink.ptms.adyeshach.common.entity.manager.ManagerService
import ink.ptms.adyeshach.common.util.safeDistance
import ink.ptms.adyeshach.impl.entity.manager.DefaultManager
import ink.ptms.adyeshach.impl.entity.manager.DefaultPlayerManager
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.LifeCycle
import taboolib.common.TabooLibCommon
import taboolib.common.platform.function.registerBukkitListener
import java.util.concurrent.ConcurrentHashMap

/**
 * Adyeshach
 * ink.ptms.adyeshach.internal.nms.DefaultAdyeshachAPI
 *
 * @author 坏黑
 * @since 2022/6/18 16:20
 */
class DefaultAdyeshachAPI : AdyeshachAPI {

    val entityFinder = DefaultAdyeshachEntityFinder()
    val entitySerializer = DefaultAdyeshachEntitySerializer()
    val entityTypeHelper = DefaultAdyeshachEntityTypeHandler()
    val entityMetadataHandler = DefaultAdyeshachEntityMetadataHandler()
    val entityControllerHandler = DefaultAdyeshachEntityControllerHandler()
    val hologramHandler = DefaultAdyeshachHologramHandler()
    val ketherHandler = DefaultAdyeshachKetherHandler()
    val minecraftAPI = DefaultAdyeshachMinecraftAPI()
    val networkAPI = DefaultAdyeshachNetworkAPI()
    val language = DefaultAdyeshachLanguage()

    val publicEntityManager = DefaultManager()
    val publicEntityManagerTemp = DefaultManager()

    init {
        TabooLibCommon.postpone(LifeCycle.ENABLE) {
            // 释放资源
            registerBukkitListener(PlayerQuitEvent::class.java) {
                playerEntityManagerMap.remove(it.player.name)
            }
        }
    }

    override fun setupEntityManager(player: Player) {
        if (player.isOnline) {
            // 公共管理器
            getPublicEntityManager().getEntities { it.visibleAfterLoaded }.forEach {
                it.viewPlayers.viewers += player.name
            }
            getPublicEntityManager(true).getEntities { it.visibleAfterLoaded }.forEach {
                it.viewPlayers.viewers += player.name
            }
            // 私有管理器
            (getPrivateEntityManager(player) as ManagerService).onEnable()
        }
    }

    override fun releaseEntityManager(player: Player) {
        // 公共管理器
        getPublicEntityManager().getEntities { it.visibleAfterLoaded }.forEach {
            it.removeViewer(player)
        }
        getPublicEntityManager(true).getEntities { it.visibleAfterLoaded }.forEach {
            it.removeViewer(player)
        }
        // 私有管理器
        (getPrivateEntityManager(player) as ManagerService).onSave()
    }

    override fun refreshEntityManager(player: Player) {
        // 对范围内可视且在观察者列表的实体进行刷新
        entityFinder.getEntities(player) { it.isViewer(player) && it.getLocation().safeDistance(player.location) < it.visibleDistance }.forEach {
            it.visible(player, true)
        }
    }

    override fun getPublicEntityManager(temporary: Boolean): Manager {
        return if (temporary) publicEntityManagerTemp else publicEntityManager
    }

    override fun getPrivateEntityManager(player: Player, temporary: Boolean): Manager {
        return if (playerEntityManagerMap.containsKey(player.name)) {
            playerEntityManagerMap[player.name]!!
        } else {
            val manager = DefaultPlayerManager(player)
            playerEntityManagerMap[player.name] = manager
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

        val playerEntityManagerMap = ConcurrentHashMap<String, Manager>()
    }
}