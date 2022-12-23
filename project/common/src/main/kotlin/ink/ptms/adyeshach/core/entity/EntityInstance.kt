package ink.ptms.adyeshach.core.entity

import ink.ptms.adyeshach.core.bukkit.BukkitAnimation
import ink.ptms.adyeshach.core.bukkit.data.EntityPosition
import ink.ptms.adyeshach.core.entity.manager.Manager
import org.bukkit.Location

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.EntityInstance
 *
 * @author 坏黑
 * @since 2022/6/15 22:57
 */
@Suppress("SpellCheckingInspection")
interface EntityInstance : EntityBase, Controllable, GenericEntity, Rideable, Viewable {

    /**
     * 实体序号，用于发包
     */
    val index: Int

    /**
     * 单位管理器
     */
    var manager: Manager?

    /**
     * 是否傻子（即禁用 AI）
     */
    var isNitwit: Boolean

    /**
     * 移动目标
     */
    var moveTarget: Location?

    /**
     * 取决于是否被公开的单位管理器管理
     */
    fun isPublic(): Boolean

    /**
     * 是否为临时实体，即非持久化储存
     */
    fun isTemporary(): Boolean

    /**
     * 生成实体，会覆盖相同 index 的实体。
     */
    fun spawn(location: Location)

    /**
     * 重新生成实体
     */
    fun respawn()

    /**
     * 销毁实体
     * @param destroyPacket 是否销毁数据包
     * @param removeFromManager 是否从管理器中移除
     */
    fun despawn(destroyPacket: Boolean = true, removeFromManager: Boolean = false)

    /**
     * 销毁实体，并从管理器中移除
     */
    fun remove() = despawn(removeFromManager = true)

    /**
     * 修改实体位置
     */
    fun teleport(location: Location)

    /**
     * 修改实体位置
     */
    fun teleport(entityPosition: EntityPosition)

    /**
     * 修改实体位置
     */
    fun teleport(x: Double, y: Double, z: Double)

    /**
     * 修改实体动量
     */
    fun setVelocity(vector: org.bukkit.util.Vector)

    /**
     * 修改实体动量
     */
    fun setVelocity(x: Double, y: Double, z: Double)

    /**
     * 修改实体视角
     * @param location 位置
     * @param forceUpdate 强制更新
     */
    fun setHeadRotation(location: Location, forceUpdate: Boolean = false)

    /**
     * 修改实体视角
     * @param yaw 偏航角
     * @param pitch 俯仰角
     * @param forceUpdate 强制更新
     */
    fun setHeadRotation(yaw: Float, pitch: Float, forceUpdate: Boolean = false)

    /**
     * 播放动画数据包
     *
     * @param animation [BukkitAnimation]
     */
    fun sendAnimation(animation: BukkitAnimation)

    /**
     * 克隆实体
     *
     * @param newId 新的实体序号
     * @param location 位置
     * @param manager 单位管理器
     */
    fun clone(newId: String, location: Location, manager: Manager? = null): EntityInstance?

    /**
     * 发送客户端移动量数据包
     */
    @Deprecated("请使用 setVelocity(vector)", ReplaceWith("setVelocity(vector)"))
    fun sendVelocity(vector: org.bukkit.util.Vector)
}