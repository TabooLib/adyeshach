package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.core.*
import ink.ptms.adyeshach.core.entity.manager.EventBus
import ink.ptms.adyeshach.core.entity.manager.ManagerType
import ink.ptms.adyeshach.impl.manager.*
import ink.ptms.adyeshach.impl.storage.EntityStorage
import org.bukkit.entity.Player
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.PlatformFactory
import taboolib.common.platform.function.submitAsync
import taboolib.platform.util.removeMeta
import taboolib.platform.util.setMeta
import java.util.concurrent.ConcurrentHashMap

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.DefaultAdyeshachAPI
 *
 * @author 坏黑
 * @since 2022/6/18 16:20
 */
@RuntimeDependencies(
    RuntimeDependency(
        "!com.github.ben-manes.caffeine:caffeine:2.9.3",
        test = "!com.github.benmanes.caffeine_2_9_3.cache.Caffeine",
        relocate = ["!com.github.benmanes.caffeine", "!com.github.benmanes.caffeine_2_9_3"]
    )
)
class DefaultAdyeshachAPI : AdyeshachAPI {

    /** 单位检索接口 **/
    var localEntityFinder = PlatformFactory.getAPI<AdyeshachEntityFinder>()

    /** 单位序列化接口 **/
    var localEntitySerializer = PlatformFactory.getAPI<AdyeshachEntitySerializer>()

    /** 单位类型管理接口 **/
    var localEntityTypeHelper = PlatformFactory.getAPI<AdyeshachEntityTypeRegistry>()

    /** 单位元数据管理接口 **/
    var localEntityMetadataHandler = PlatformFactory.getAPI<AdyeshachEntityMetadataRegistry>()

    /** 单位控制器管理接口 **/
    var localEntityControllerHandler = PlatformFactory.getAPI<AdyeshachEntityControllerRegistry>()

    /** 全系接口 **/
    var localHologramHandler = PlatformFactory.getAPI<AdyeshachHologramHandler>()

    /** 脚本接口 **/
    var localKetherHandler = PlatformFactory.getAPI<AdyeshachKetherHandler>()

    /** 服务端逆向操作工具 **/
    var localMinecraftAPI = PlatformFactory.getAPI<AdyeshachMinecraftAPI>()

    /** 网络工具 **/
    var localNetworkAPI = PlatformFactory.getAPI<AdyeshachNetworkAPI>()

    /** 语言文件接口 **/
    var localLanguage = PlatformFactory.getAPI<AdyeshachLanguage>()

    /** 公共单位管理器 **/
    var localPublicEntityManager = LocalPersistentManager()

    /** 公共单位管理器（孤立） **/
    var localPublicEntityManagerIsolated = BaseManager()

    /** 公共单位管理器（临时） **/
    var localPublicEntityManagerTemporary = DefaultManager()

    override fun setupEntityManager(player: Player) {
        if (!player.hasMetadata("adyeshach_setup")) {
            // 设置标签避免重复执行
            player.setMeta("adyeshach_setup", true)
            // 公共管理器
            getPublicEntityManager(ManagerType.PERSISTENT).getEntities { it.visibleAfterLoaded }.forEach { it.viewPlayers.viewers += player.name }
            getPublicEntityManager(ManagerType.TEMPORARY).getEntities { it.visibleAfterLoaded }.forEach { it.viewPlayers.viewers += player.name }
            // 是否启用持久化私有管理器
            if (EntityStorage.isEnabled()) {
                // 异步加载私有管理器
                submitAsync { getPrivateEntityManager(player, ManagerType.PERSISTENT).onEnable() }
            }
        }
    }

    override fun releaseEntityManager(player: Player) {
        if (player.hasMetadata("adyeshach_setup")) {
            player.removeMeta("adyeshach_setup")
            // 公共管理器
            getPublicEntityManager(ManagerType.PERSISTENT).getEntities().forEach { it.removeViewer(player) }
            getPublicEntityManager(ManagerType.TEMPORARY).getEntities().forEach { it.removeViewer(player) }
            // 异步卸载私有管理器
            submitAsync {
                var privateManager = getPrivateEntityManager(player, ManagerType.TEMPORARY)
                privateManager.onDisable()
                // 是否启用持久化私有管理器
                if (EntityStorage.isEnabled()) {
                    privateManager = getPrivateEntityManager(player, ManagerType.PERSISTENT)
                    privateManager.onDisable()
                    privateManager.onSave()
                }
            }
            // 移除管理器
            playerEntityManagerMap.remove(player.name)
            playerEntityTemporaryManagerMap.remove(player.name)
        }
    }

    override fun refreshEntityManager(player: Player) {
        // 对范围内可视且在观察者列表的实体进行刷新
        localEntityFinder.getVisibleEntities(player).forEach { it.visible(player, true) }
    }

    override fun getPublicEntityManager(type: ManagerType): BaseManager {
        return when (type) {
            ManagerType.PERSISTENT -> localPublicEntityManager
            ManagerType.TEMPORARY -> localPublicEntityManagerTemporary
            ManagerType.ISOLATED -> localPublicEntityManagerIsolated
        }
    }

    override fun getPrivateEntityManager(player: Player, type: ManagerType): BaseManager {
        return when (type) {
            ManagerType.ISOLATED -> {
                BasePlayerManager(player)
            }
            ManagerType.TEMPORARY -> {
                playerEntityTemporaryManagerMap.computeIfAbsent(player.name) { DefaultPlayerManager(player) }
            }
            ManagerType.PERSISTENT -> {
                if (EntityStorage.isEnabled()) {
                    playerEntityManagerMap.computeIfAbsent(player.name) { PlayerPersistentManager(player) }
                } else {
                    error("Private persistence manager is not enabled.")
                }
            }
        }
    }

    override fun getEntityFinder(): AdyeshachEntityFinder {
        return localEntityFinder
    }

    override fun getEntitySerializer(): AdyeshachEntitySerializer {
        return localEntitySerializer
    }

    override fun getEntityTypeRegistry(): AdyeshachEntityTypeRegistry {
        return localEntityTypeHelper
    }

    override fun getEntityMetadataRegistry(): AdyeshachEntityMetadataRegistry {
        return localEntityMetadataHandler
    }

    override fun getEntityControllerRegistry(): AdyeshachEntityControllerRegistry {
        return localEntityControllerHandler
    }

    override fun getHologramHandler(): AdyeshachHologramHandler {
        return localHologramHandler
    }

    override fun getKetherHandler(): AdyeshachKetherHandler {
        return localKetherHandler
    }

    override fun getMinecraftAPI(): AdyeshachMinecraftAPI {
        return localMinecraftAPI
    }

    override fun getNetworkAPI(): AdyeshachNetworkAPI {
        return localNetworkAPI
    }

    override fun getLanguage(): AdyeshachLanguage {
        return localLanguage
    }

    override fun getEventBus(): EventBus {
        return localEventBus
    }

    companion object {

        /** 玩家单位管理器 **/
        val playerEntityManagerMap = ConcurrentHashMap<String, PlayerPersistentManager>()
        /** 玩家单位管理器（临时） **/
        val playerEntityTemporaryManagerMap = ConcurrentHashMap<String, DefaultPlayerManager>()

        /** 事件总线 */
        val localEventBus = DefaultEventBus()
    }
}