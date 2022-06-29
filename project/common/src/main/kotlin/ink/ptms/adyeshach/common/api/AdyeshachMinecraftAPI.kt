package ink.ptms.adyeshach.common.api

/**
 * 获取 Adyeshach 中所有有关 NMS 职责的接口。
 *
 * 我们不建议直接调用该接口中的任何方法，除非您了解其内部实现原理，或是熟悉 Minecraft 客户端机制。
 * 否则将会对玩家客户端造成破坏性的影响。
 *
 * @author 坏黑
 * @since 2022/6/15 18:05
 */
interface AdyeshachMinecraftAPI {

    /**
     * 获取用于转换 NMS 对象或其他调用 Minecraft 内部方法的接口
     */
    fun getHelper(): MinecraftHelper

    /**
     * 获取用于生成数据包实体的接口
     */
    fun getEntitySpawner(): MinecraftEntitySpawner

    /**
     * 获取用于控制数据包实体的接口
     */
    fun getEntityOperator(): MinecraftEntityOperator

    /**
     * 获取用于控制数据包实体元数据信息的接口
     */
    fun getEntityMetadataHandler(): MinecraftEntityMetadataHandler

    /**
     * 获取用于控制虚拟玩家相关数据包的接口
     */
    fun getEntityPlayerHandler(): MinecraftEntityPlayerHandler

    /**
     * 获取用于控制数据包的接口
     */
    fun getPacketHandler(): MinecraftPacketHandler

    /**
     * 获取用于访问世界内部信息的接口
     */
    fun getWorldAccess(): MinecraftWorldAccess
}