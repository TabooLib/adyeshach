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
    val networkAPI = DefaultAdyeshachNetworkAPI()

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
        TODO("Not yet implemented")
    }

    override fun getEntityControllerHandler(): AdyeshachEntityControllerHandler {
        TODO("Not yet implemented")
    }

    override fun getHologramHandler(): AdyeshachHologramHandler {
        TODO("Not yet implemented")
    }

    override fun getKetherHandler(): AdyeshachKetherHandler {
        TODO("Not yet implemented")
    }

    override fun getMinecraftAPI(): AdyeshachMinecraftAPI {
        TODO("Not yet implemented")
    }

    override fun getNetworkAPI(): AdyeshachNetworkAPI {
        return networkAPI
    }
}