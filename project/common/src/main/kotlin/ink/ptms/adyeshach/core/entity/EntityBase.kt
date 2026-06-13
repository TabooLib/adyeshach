package ink.ptms.adyeshach.core.entity

import ink.ptms.adyeshach.core.bukkit.data.EntityPosition
import ink.ptms.adyeshach.core.entity.path.PathType
import ink.ptms.adyeshach.core.event.AdyeshachEntityCreateEvent
import org.bukkit.Location
import org.bukkit.World
import java.util.*

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.EntityBase
 *
 * @author 坏黑
 * @since 2022/6/15 23:03
 */
interface EntityBase : Metaable, TagContainer, EntitySerializable {

    /**
     * 实体类型
     */
    val entityType: EntityTypes

    /**
     * 实体大小
     */
    val entitySize: EntitySize

    /**
     * 实体寻路方式
     */
    val entityPathType: PathType

    /**
     * 实体生成序号（名字）
     */
    var id: String

    /**
     * 实体内部序号（UUID）
     */
    val uniqueId: String

    /**
     * 实体内部序号转 UUID 类型
     */
    val normalizeUniqueId: UUID

    /**
     * 实体所在位置（直接修改该字段不会触发位置更新）
     */
    var position: EntityPosition

    /**
     * 实体身体 yaw
     * 空值表示沿用旧位置 yaw，保证旧数据向下兼容。
     */
    var bodyYaw: Float?

    /**
     * 测试状态
     */
    val isTesting: Boolean

    /**
     * 无效状态
     */
    val isInvalid: Boolean

    /**
     * 是否被删除
     */
    val isRemoved: Boolean

    /**
     * 是否完成 [AdyeshachEntityCreateEvent] 事件以及 [create] 回调函数
     * 在这之前进行 [setMetadata] 操作不会发送数据包
     */
    var isCreated: Boolean

    /**
     * 实体详细坐标信息: x
     */
    val x: Double

    /**
     * 实体详细坐标信息: y
     */
    val y: Double

    /**
     * 实体详细坐标信息: z
     */
    val z: Double

    /**
     * 区块 X
     */
    val chunkX: Int

    /**
     * 区块 Z
     */
    val chunkZ: Int

    /**
     * 实体详细坐标信息: yaw
     */
    val yaw: Float

    /**
     * 实体详细坐标信息: pitch
     */
    val pitch: Float

    /**
     * 实体所在世界
     */
    val world: World

    /**
     * 实体所在位置（封装自 EntityPosition 类型）
     */
    fun getLocation(): Location

    /**
     * 实体身体所在位置
     * 生成和传送数据包使用该位置写入身体 yaw，旧位置 yaw 语义保持不变。
     */
    fun getBodyLocation(): Location

    /**
     * 实体眼部所在位置
     */
    fun getEyeLocation(): Location

    /**
     * 创建一个空的实体
     */
    fun createEmpty(): EntityBase
}
