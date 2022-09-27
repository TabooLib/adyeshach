package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.common.api.*
import taboolib.module.nms.nmsProxy

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.DefaultAdyeshachMinecraftAPI
 *
 * @author 坏黑
 * @since 2022/6/28 00:06
 */
class DefaultAdyeshachMinecraftAPI : AdyeshachMinecraftAPI {

    val group = "ink.ptms.adyeshach.impl.nms"
    val nmsHelper = nmsProxy<MinecraftHelper>("$group.DefaultMinecraftHelper")
    val nmsEntitySpawner = nmsProxy<MinecraftEntitySpawner>("$group.DefaultMinecraftEntitySpawner")
    val nmsEntityOperator = nmsProxy<MinecraftEntityOperator>("$group.DefaultMinecraftEntityOperator")
    val nmsEntityMetadataHandler = nmsProxy<MinecraftEntityMetadataHandler>("$group.DefaultMinecraftEntityMetadataHandler")
    val nmsEntityPlayerHandler = nmsProxy<MinecraftEntityPlayerHandler>("$group.DefaultMinecraftEntityPlayerHandler")
    val nmsPacketHandler = nmsProxy<MinecraftPacketHandler>("$group.DefaultMinecraftPacketHandler")
    val nmsWorldAccess = nmsProxy<MinecraftWorldAccess>("$group.DefaultMinecraftWorldAccess")

    override fun getHelper(): MinecraftHelper {
        return nmsHelper
    }

    override fun getEntitySpawner(): MinecraftEntitySpawner {
        return nmsEntitySpawner
    }

    override fun getEntityOperator(): MinecraftEntityOperator {
        return nmsEntityOperator
    }

    override fun getEntityMetadataHandler(): MinecraftEntityMetadataHandler {
        return nmsEntityMetadataHandler
    }

    override fun getEntityPlayerHandler(): MinecraftEntityPlayerHandler {
        return nmsEntityPlayerHandler
    }

    override fun getPacketHandler(): MinecraftPacketHandler {
        return nmsPacketHandler
    }

    override fun getWorldAccess(): MinecraftWorldAccess {
        return nmsWorldAccess
    }
}