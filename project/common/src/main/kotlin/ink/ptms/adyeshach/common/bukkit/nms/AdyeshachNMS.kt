package ink.ptms.adyeshach.common.bukkit.nms

import taboolib.common.platform.PlatformFactory

/**
 * 获取 Adyeshach 中所有有关 NMS 职责的接口。
 *
 * 我们不建议直接调用该接口中的任何方法，除非您了解其内部实现原理，或是熟悉 Minecraft 客户端机制。
 * 否则将会对玩家客户端造成破坏性的影响。
 *
 * @author 坏黑
 * @since 2022/6/15 18:05
 */
object AdyeshachNMS {

    /**
     * 获取用于转换 NMS 对象或其他调用 Minecraft 内部方法的接口
     */
    fun getHelper() = PlatformFactory.getAPI<MinecraftHelper>()

    /**
     * 获取用于生成数据包实体的接口
     */
    fun getEntitySpawner() = PlatformFactory.getAPI<MinecraftEntitySpawner>()

    /**
     * 获取用于控制数据包实体的接口
     */
    fun getEntityOperator() = PlatformFactory.getAPI<MinecraftEntityOperator>()

    /**
     * 获取用于控制数据包实体元数据信息的接口
     */
    fun getEntityMetadataHandler() = PlatformFactory.getAPI<MinecraftEntityMetadataHandler>()

    /**
     * 获取用于控制虚拟玩家相关数据包的接口
     */
    fun getMinecraftEntityPlayerHandler() = PlatformFactory.getAPI<MinecraftEntityPlayerHandler>()

    /**
     * 获取用于控制数据包的接口
     */
    fun getPacketHandler() = PlatformFactory.getAPI<MinecraftPacketHandler>()

    /**
     * 获取用于访问世界内部信息的接口
     */
    fun getWorldAccess() = PlatformFactory.getAPI<MinecraftWorldAccess>()
}