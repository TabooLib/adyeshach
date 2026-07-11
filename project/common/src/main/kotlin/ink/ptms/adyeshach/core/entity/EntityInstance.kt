package ink.ptms.adyeshach.core.entity

import ink.ptms.adyeshach.core.bukkit.BukkitAnimation
import ink.ptms.adyeshach.core.bukkit.data.EntityPosition
import ink.ptms.adyeshach.core.entity.manager.Manager
import ink.ptms.adyeshach.core.entity.path.InterpolatedLocation
import org.bukkit.Location
import org.bukkit.util.Vector
import taboolib.common5.Baffle

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.EntityInstance
 *
 * @author 坏黑
 * @since 2022/6/15 22:57
 */
interface EntityInstance : EntityBase, Controllable, GenericEntity, Rideable, Viewable, TickService, Companionable {

    /**
     * 实体序号，用于发包
     */
    val index: Int

    /**
     * 单位管理器
     */
    var manager: Manager?

    /**
     * 是否傻子（禁用 AI / 控制器 / 路径推进，不影响位置同步）
     *
     * 为 true 时跳过 brain、bionicSight 与 moveFrames 推进。
     * 1.21.8 起客户端对实体传送与相对移动的插值/动画判定更严，nitwit 实体若每帧硬传送会表现为平移；
     * 因此 nitwit 仍走 clientPosition 的相对移动同步，仅关闭 AI。
     */
    var isNitwit: Boolean

    /**
     * 控制器调度器
     */
    var brain: Brain?

    /**
     * 客户端位置修正时间
     */
    var clientPositionFixed: Long

    /**
     * 客户端位置更新间隔
     */
    var clientPositionUpdateInterval: Baffle

    /**
     * 是否忽略客户端位置更新间隔
     */
    var isIgnoredClientPositionUpdateInterval: Boolean

    /**
     * 移动定位
     */
    var moveFrames: InterpolatedLocation?

    /**
     * 移动目标
     */
    var moveTarget: Location?

    /**
     * 是否启用客户端 ID 对应表
     */
    var useClientEntityMap: Boolean

    /**
     * 是否允许生成时覆盖视角
     */
    var isRotationFixOnSpawn: Boolean

    /**
     * 是否允许生成时刷新 Passenger
     */
    var isPassengerRefreshOnSpawn: Boolean

    /**
     * 是否禁用 Visible 事件
     * 将优化大量实体生成时的性能
     */
    var isDisableVisibleEvent: Boolean

    /**
     * 是否在 Tick 中禁用载具检查
     * 特定场景下可以提升性能
     */
    var isDisableVehicleCheckOnTick: Boolean

    /**
     * 是否在 Tick 中禁用载具视角同步
     * 特定场景下可以提升性能
     */
    var isDisableVehicleRotationSync: Boolean

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
     * 如果实体已从管理器中移除则会抛出异常
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
    fun setVelocity(vector: Vector)

    /**
     * 修改实体动量
     */
    fun setVelocity(x: Double, y: Double, z: Double)

    /**
     * 获取实体动量
     */
    fun getVelocity(): Vector

    /**
     * 修改实体视角
     * @param location 位置
     * @param forceUpdate 强制更新
     */
    fun setHeadRotation(location: Location, forceUpdate: Boolean = false)

    /**
     * 修改实体头部和身体朝向
     * 显式看向目标位置时使用该入口，保证当帧身体 yaw 与头部 yaw 对齐。
     *
     * @param location 看向目标位置
     */
    fun setHeadAndBodyRotation(location: Location)

    /**
     * 修改实体头部和身体朝向
     * 显式写入角度时使用该入口，保证当帧身体 yaw 与头部 yaw 对齐。
     *
     * @param yaw 偏航角
     * @param pitch 俯仰角
     */
    fun setHeadAndBodyRotation(yaw: Float, pitch: Float)

    /**
     * 修改实体视角
     * @param yaw 偏航角
     * @param pitch 俯仰角
     * @param forceUpdate 强制更新
     */
    fun setHeadRotation(yaw: Float, pitch: Float, forceUpdate: Boolean = false)

    /**
     * 修改实体身体朝向
     *
     * @param yaw 身体偏航角
     */
    fun setBodyRotation(yaw: Float)

    /**
     * 微调页展示的身体 yaw（运行时 clientBodyPosition，与画面一致；非仅存档 bodyYaw）
     */
    fun displayBodyYaw(): Float

    /**
     * 播放动画数据包
     * @param animation [BukkitAnimation]
     */
    fun sendAnimation(animation: BukkitAnimation)

    /**
     * 播放受击动画（1.19.4+）
     * @param yaw 控制实体受击时身体倾斜的方向。角度值决定实体向哪个方向"歪"，模拟被从某个方向击中的视觉效果。
     */
    fun sendHurtAnimation(yaw: Float)

    /**
     * 添加附着单位
     * 附着单位是临时的，不会被持久化
     * 随着主实体的移动而移动
     *
     * @param id 附着单位序号
     * @param relativePos 相对位置
     */
    fun addAttachEntity(id: Int, relativePos: Vector)

    /**
     * 移除附着单位
     */
    fun removeAttachEntity(id: Int)

    /**
     * 获取所有附着单位
     */
    fun getAttachEntities(): Map<Int, Vector>

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
    fun sendVelocity(vector: Vector)

    /**
     * 重新校准位置
     */
    fun refreshPosition()
}
