package ink.ptms.adyeshach.common.entity

import ink.ptms.adyeshach.common.bukkit.BukkitAnimation
import ink.ptms.adyeshach.common.bukkit.data.EntityPosition
import ink.ptms.adyeshach.common.entity.manager.Manager
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.util.Vector

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.entity.EntityInstance
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
     * 是否被删除，执行 delete() 方法后该属性为 true，该属性仅做标记无实际意义
     */
    var isDeleted: Boolean

    /**
     * 是否公开取决于 manager 是否为 ManagerPublic 即由谁管理，而非其他参数
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
     * 销毁实体，从玩家视野中移除该实体
     */
    fun destroy()

    /**
     * 删除实体
     * + 从管理器中移除，不再接受托管，但不会销毁实体
     * + 被移除管理器后该单位将失去所有主动行为（包括玩家类型的皮肤刷新）
     * + 同时该单位将无法被指令控制
     */
    fun remove()

    /**
     * 删除实体，并销毁
     */
    fun delete()

    /**
     * 修改实体位置
     */
    fun teleport(location: Location)

    /**
     * 修改实体位置
     */
    fun teleport(entityPosition: EntityPosition)

    /**
     * 修改实体位置，并继承实体本身的视角
     */
    fun teleport(x: Double, y: Double, z: Double)

    /**
     * 发送动量数据包
     */
    fun setVelocity(vector: Vector)

    /**
     * 修改实体视角
     */
    fun setHeadRotation(yaw: Float, pitch: Float)

    /**
     * 播放动画数据包
     */
    fun setAnimation(animation: BukkitAnimation)
}