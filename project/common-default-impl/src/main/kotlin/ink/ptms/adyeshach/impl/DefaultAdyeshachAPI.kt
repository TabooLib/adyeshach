package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.common.api.*
import ink.ptms.adyeshach.common.entity.manager.Manager
import org.bukkit.entity.Player

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

    override fun getPublicEntityManager(temporary: Boolean): Manager {
        TODO("Not yet implemented")
    }

    override fun getPrivateEntityManager(player: Player, temporary: Boolean): Manager {
        TODO("Not yet implemented")
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
}