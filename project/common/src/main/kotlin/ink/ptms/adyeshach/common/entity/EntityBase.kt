package ink.ptms.adyeshach.common.entity

import ink.ptms.adyeshach.common.bukkit.data.EntityPosition
import org.bukkit.Location
import org.bukkit.World
import java.util.*

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.entity.EntityBase
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
     * 测试状态
     */
    var testing: Boolean

    /**
     * 无效状态
     */
    var invalid: Boolean

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
    fun getWorld(): World

    /**
     * 实体所在位置（封装自 EntityPosition 类型）
     */
    fun getLocation(): Location

    /**
     * 创建一个空的实体
     */
    fun createEmpty(): EntityBase
}