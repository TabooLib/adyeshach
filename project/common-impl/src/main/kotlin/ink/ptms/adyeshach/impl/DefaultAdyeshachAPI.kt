package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.core.*
import ink.ptms.adyeshach.core.entity.manager.EventBus
import ink.ptms.adyeshach.core.entity.manager.ManagerType
import ink.ptms.adyeshach.core.event.AdyeshachPlayerSetupEvent
import ink.ptms.adyeshach.impl.manager.*
import org.bukkit.entity.Player
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.PlatformFactory
import taboolib.common.platform.function.warning
import taboolib.common.util.t
import taboolib.platform.util.PlayerSessionMap
import taboolib.platform.util.removeMeta
import taboolib.platform.util.setMeta

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

    /** 全息接口 **/
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
            // 已完成状态加载
            AdyeshachPlayerSetupEvent(player).call()
        } else {
            // 重复执行警告
            warning(
                """
                    玩家 ${player.name} 的实体管理器已经初始化。
                    Player ${player.name} has already initialized entity manager.
                """.t()
            )
        }
    }

    override fun releaseEntityManager(player: Player, async: Boolean) {
        if (player.hasMetadata("adyeshach_setup")) {
            player.removeMeta("adyeshach_setup")
            // 公共管理器
            getPublicEntityManager(ManagerType.PERSISTENT).getEntities().forEach { it.releaseViewerCache(player) }
            getPublicEntityManager(ManagerType.TEMPORARY).getEntities().forEach { it.releaseViewerCache(player) }
        }
        // 无论 metadata 是否存在，都尝试清理私有管理器
        // 避免因 metadata 被外部移除而跳过清理导致内存泄漏
        val manager = playerEntityTemporaryManagerMap[player.uniqueId]
        // 玩家退出时连接已断开，私有实体只需要释放服务端缓存和管理器索引，不再向该玩家发送 destroy 包。
        manager?.getEntities()?.toList()?.forEach {
            it.releaseViewerCache(player)
            manager.remove(it)
        }
        playerEntityTemporaryManagerMap.remove(player)
        // 清理玩家的可见实体索引（兜底）
        localEntityFinder.clearPlayerVisibleEntities(player)
    }

    override fun refreshEntityManager(player: Player) {
        // 对范围内可视且在观察者列表的实体进行刷新
        localEntityFinder.getVisibleEntities(player).forEach { it.visible(player, true) }
    }

    override fun refreshPublicEntityManager() {
        localPublicEntityManager.getEntities().forEach {
            it.despawn()
            it.spawn(it.getLocation())
        }
    }

    override fun getPublicEntityManager(type: ManagerType): BaseManager {
        return when (type) {
            ManagerType.PERSISTENT -> localPublicEntityManager
            ManagerType.TEMPORARY -> localPublicEntityManagerTemporary
            ManagerType.ISOLATED -> localPublicEntityManagerIsolated
        }
    }

    override fun getPrivateEntityManager(player: Player, type: ManagerType): BaseManager {
        if (!player.isOnline) {
            error(
                """
                    玩家 ${player.name} 不是在线状态，无法获取其私有实体管理器。
                    Player ${player.name} is not online, cannot get its private entity manager.
                """.trimIndent()
            )
        }
        return when (type) {
            ManagerType.ISOLATED -> IsolatedPlayerManager(player)
            ManagerType.TEMPORARY -> playerEntityTemporaryManagerMap.getOrCreate(player.uniqueId) { DefaultPlayerManager(player) }!!
            else -> error(
                """
                    不支持该类型的管理器: $type
                    Unsupported manager type: $type
                """.t()
            )
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

        /** 玩家单位管理器（临时） **/
        val playerEntityTemporaryManagerMap = PlayerSessionMap<DefaultPlayerManager>()

        /** 事件总线 */
        val localEventBus = DefaultEventBus()
    }
}
