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

    val nmsHelper = nmsProxy<MinecraftHelper>("ink.ptms.adyeshach.impl.nms.DefaultMinecraftHelper")
    val nmsEntitySpawner = nmsProxy<MinecraftEntitySpawner>("ink.ptms.adyeshach.impl.nms.DefaultMinecraftEntitySpawner")
    val nmsEntityOperator = nmsProxy<MinecraftEntityOperator>("ink.ptms.adyeshach.impl.nms.DefaultMinecraftEntityOperator")
    val nmsEntityMetadataHandler = nmsProxy<MinecraftEntityMetadataHandler>("ink.ptms.adyeshach.impl.nms.DefaultMinecraftEntityMetadataHandler")
    val nmsEntityPlayerHandler = nmsProxy<MinecraftEntityPlayerHandler>("ink.ptms.adyeshach.impl.nms.DefaultMinecraftEntityPlayerHandler")
    val nmsPacketHandler = nmsProxy<MinecraftPacketHandler>("ink.ptms.adyeshach.impl.nms.DefaultMinecraftPacketHandler")
    val nmsWorldAccess = nmsProxy<MinecraftWorldAccess>("ink.ptms.adyeshach.impl.nms.DefaultMinecraftWorldAccess")

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

    override fun getMinecraftEntityPlayerHandler(): MinecraftEntityPlayerHandler {
        return nmsEntityPlayerHandler
    }

    override fun getPacketHandler(): MinecraftPacketHandler {
        return nmsPacketHandler
    }

    override fun getWorldAccess(): MinecraftWorldAccess {
        return nmsWorldAccess
    }
}