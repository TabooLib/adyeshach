package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.core.*
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.PlatformFactory
import taboolib.common.platform.function.info
import taboolib.common.platform.function.releaseResourceFile
import taboolib.common.util.unsafeLazy
import taboolib.common5.cdouble
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import taboolib.module.configuration.util.mapValue
import taboolib.module.nms.nmsProxy
import java.util.concurrent.CompletableFuture

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
    val nmsHelper by lazy {
        proxy<MinecraftHelper>("$group.DefaultMinecraftHelper")
    }

    /** 单位生成接口 **/
    val nmsEntitySpawner by lazy {
        proxy<MinecraftEntitySpawner>("$group.DefaultMinecraftEntitySpawner")
    }

    /** 单位控制接口 **/
    val nmsEntityOperator by lazy {
        proxy<MinecraftEntityOperator>("$group.DefaultMinecraftEntityOperator")
    }

    /** 元数据控制接口 **/
    val nmsEntityMetadataHandler by lazy {
        proxy<MinecraftEntityMetadataHandler>("$group.DefaultMinecraftEntityMetadataHandler")
    }

    /** 玩家控制接口 **/
    val nmsEntityPlayerHandler by lazy {
        proxy<MinecraftEntityPlayerHandler>("$group.DefaultMinecraftEntityPlayerHandler")
    }

    /** 记分板控制接口 **/
    val nmsScoreboardOperator by lazy {
        proxy<MinecraftScoreboardOperator>("$group.DefaultMinecraftScoreboardOperator")
    }

    /** 数据包控制接口 **/
    val nmsPacketHandler by lazy {
        proxy<MinecraftPacketHandler>("$group.DefaultMinecraftPacketHandler")
    }

    /** 世界访问接口 **/
    val nmsWorldAccess by lazy {
        proxy<MinecraftWorldAccess>("$group.DefaultMinecraftWorldAccess")
    }

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

    inline fun <reified T> proxy(bind: String, vararg parameter: Any): T {
        val time = System.currentTimeMillis()
        val proxy = nmsProxy(T::class.java, bind, *parameter)
        info("Generate ${T::class.java.simpleName} in ${System.currentTimeMillis() - time}ms")
        return proxy
    }

    init {
        CompletableFuture.runAsync {
            nmsHelper
            nmsEntitySpawner
            nmsEntityOperator
            nmsEntityMetadataHandler
            nmsEntityPlayerHandler
            nmsScoreboardOperator
            nmsPacketHandler
            nmsWorldAccess
        }
    }

    companion object {

        val blockHeight by unsafeLazy {
            Configuration.loadFromInputStream(releaseResourceFile("core/block_height.json", true).readBytes().inputStream(), Type.JSON)
        }

        val blockHeightMap by unsafeLazy {
            blockHeight.mapValue { it.cdouble }
        }

        @Awake(LifeCycle.CONST)
        fun init() {
            PlatformFactory.registerAPI<AdyeshachMinecraftAPI>(DefaultAdyeshachMinecraftAPI())
        }
    }
}