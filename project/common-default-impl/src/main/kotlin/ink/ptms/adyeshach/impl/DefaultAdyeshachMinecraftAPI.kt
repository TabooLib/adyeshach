package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.common.api.*
import ink.ptms.adyeshach.impl.nms.*

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.DefaultAdyeshachMinecraftAPI
 *
 * @author 坏黑
 * @since 2022/6/28 00:06
 */
class DefaultAdyeshachMinecraftAPI : AdyeshachMinecraftAPI {

    val helper = DefaultMinecraftHelper()
    val entitySpawner = DefaultMinecraftEntitySpawner()
    val entityOperator = DefaultMinecraftEntityOperator()
    val entityMetadataHandler = DefaultMinecraftEntityMetadataHandler()
    val entityPlayerHandler = DefaultMinecraftEntityPlayerHandler()
    val packetHandler = DefaultMinecraftPacketHandler()
    val worldAccess = DefaultMinecraftWorldAccess()

    override fun getHelper(): MinecraftHelper {
        return helper
    }

    override fun getEntitySpawner(): MinecraftEntitySpawner {
        return entitySpawner
    }

    override fun getEntityOperator(): MinecraftEntityOperator {
        return entityOperator
    }

    override fun getEntityMetadataHandler(): MinecraftEntityMetadataHandler {
        return entityMetadataHandler
    }

    override fun getMinecraftEntityPlayerHandler(): MinecraftEntityPlayerHandler {
        return entityPlayerHandler
    }

    override fun getPacketHandler(): MinecraftPacketHandler {
        return packetHandler
    }

    override fun getWorldAccess(): MinecraftWorldAccess {
        return worldAccess
    }
}