package ink.ptms.adyeshach.internal

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

    val networkAPI = DefaultAdyeshachNetworkAPI()

    override fun getPublicEntityManager(temporary: Boolean): Manager {
        TODO("Not yet implemented")
    }

    override fun getPrivateEntityManager(player: Player, temporary: Boolean): Manager {
        TODO("Not yet implemented")
    }

    override fun getEntityFinder(): AdyeshachEntityFinder {
        TODO("Not yet implemented")
    }

    override fun getEntitySerializer(): AdyeshachEntitySerializer {
        TODO("Not yet implemented")
    }

    override fun getEntityTypeHandler(): AdyeshachEntityTypeHandler {
        TODO("Not yet implemented")
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