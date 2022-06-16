package ink.ptms.adyeshach.common.entity

import ink.ptms.adyeshach.common.bukkit.data.EntityPosition
import org.bukkit.Location
import org.bukkit.World
import taboolib.library.configuration.ConfigurationSection
import java.util.*
import java.util.function.Function

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.entity.EntityBase
 *
 * @author 坏黑
 * @since 2022/6/15 23:03
 */
interface EntityBase : Metaable, TagContainer {

    /**
     * 实体类型
     */
    val entityType: EntityTypes

    /**
     * 实体生成序号（名字）
     */
    val id: String

    /**
     * 实体内部序号（UUID）
     */
    val uniqueId: String

    /**
     * 实体内部序号转 UUID 类型
     */
    val normalizeUniqueId: UUID

    /**
     * 实体所在位置
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

    val x: Double

    val y: Double

    val z: Double

    val yaw: Float

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
     * 转换为 Json 字符串
     */
    fun toJson(): String

    /**
     * 转换为 Yaml 并写入 ConfigurationSection 对象
     *
     * @param transfer 节点名称转换函数
     */
    fun toYaml(section: ConfigurationSection, transfer: Function<String, String> = Function { it })
}