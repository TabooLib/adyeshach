package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.common.api.*
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

    /** 单位检索接口 **/
    var entityFinder = DefaultAdyeshachEntityFinder()

    /** 单位序列化接口 **/
    var entitySerializer = DefaultAdyeshachEntitySerializer()

    /** 单位类型管理接口 **/
    var entityTypeHelper = DefaultAdyeshachEntityTypeHandler()

    /** 单位元数据管理接口 **/
    var entityMetadataHandler = DefaultAdyeshachEntityMetadataHandler()

    /** 单位控制器管理接口 **/
    var entityControllerHandler = DefaultAdyeshachEntityControllerHandler()

    /** 全系接口 **/
    var hologramHandler = DefaultAdyeshachHologramHandler()

    /** 脚本接口 **/
    var ketherHandler = DefaultAdyeshachKetherHandler()

    /** 服务端逆向操作工具 **/
    var minecraftAPI = DefaultAdyeshachMinecraftAPI()

    /** 网络工具 **/
    var networkAPI = DefaultAdyeshachNetworkAPI()

    /** 语言文件接口 **/
    var language = DefaultAdyeshachLanguage()

    /** 公共单位管理器 **/
    var publicEntityManager = DefaultManager()

    /** 公共单位管理器（临时） **/
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
        entityFinder.getVisibleEntities(player).forEach { it.visible(player, true) }
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