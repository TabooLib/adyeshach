package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.core.*
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.PlatformFactory
import taboolib.common.platform.function.releaseResourceFile
import taboolib.common5.cdouble
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import taboolib.module.configuration.util.mapValue
import taboolib.module.nms.nmsProxy

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.DefaultAdyeshachMinecraftAPI
 *
 * @author 坏黑
 * @since 2022/6/28 00:06
 */
class DefaultAdyeshachMinecraftAPI : AdyeshachMinecraftAPI {

    val group = "${DefaultAdyeshachMinecraftAPI::class.java.`package`.name}.nms"

    /** 杂项 **/
    val nmsHelper = nmsProxy<MinecraftHelper>("$group.DefaultMinecraftHelper")

    /** 单位生成接口 **/
    val nmsEntitySpawner = nmsProxy<MinecraftEntitySpawner>("$group.DefaultMinecraftEntitySpawner")

    /** 单位控制接口 **/
    val nmsEntityOperator = nmsProxy<MinecraftEntityOperator>("$group.DefaultMinecraftEntityOperator")

    /** 元数据控制接口 **/
    val nmsEntityMetadataHandler = nmsProxy<MinecraftEntityMetadataHandler>("$group.DefaultMinecraftEntityMetadataHandler")

    /** 玩家控制接口 **/
    val nmsEntityPlayerHandler = nmsProxy<MinecraftEntityPlayerHandler>("$group.DefaultMinecraftEntityPlayerHandler")

    /** 记分板控制接口 **/
    val nmsScoreboardOperator = nmsProxy<MinecraftScoreboardOperator>("$group.DefaultMinecraftScoreboardOperator")

    /** 数据包控制接口 **/
    val nmsPacketHandler = nmsProxy<MinecraftPacketHandler>("$group.DefaultMinecraftPacketHandler")

    /** 世界访问接口 **/
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

    override fun getScoreboardOperator(): MinecraftScoreboardOperator {
        return nmsScoreboardOperator
    }

    override fun getPacketHandler(): MinecraftPacketHandler {
        return nmsPacketHandler
    }

    override fun getWorldAccess(): MinecraftWorldAccess {
        return nmsWorldAccess
    }

    companion object {

        val blockHeight = Configuration.loadFromInputStream(releaseResourceFile("core/block_height.json", true).readBytes().inputStream(), Type.JSON)
        val blockHeightMap = blockHeight.mapValue { it.cdouble }

        @Awake(LifeCycle.INIT)
        fun init() {
            PlatformFactory.registerAPI<AdyeshachMinecraftAPI>(DefaultAdyeshachMinecraftAPI())
        }
    }
}