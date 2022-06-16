package ink.ptms.adyeshach.common.entity

import ink.ptms.adyeshach.common.bukkit.nms.MinecraftMeta
import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.entity.Metaable
 *
 * @author 坏黑
 * @since 2022/6/15 23:04
 */
interface Metaable {

    /**
     * 获取实体元数据
     */
    fun <T> getMetadata(key: String): T

    /**
     * 设置实体元数据
     */
    fun setMetadata(key: String, value: Any): Boolean

    /**
     * 获取实体所有可编辑的元数据模型
     */
    fun getEditableEntityMeta(): List<Meta<*>>

    /**
     * 获取实体所有元数据模型
     */
    fun getAvailableEntityMeta(): List<Meta<*>>

    /**
     * 更新实体元数据
     */
    fun updateEntityMetadata()

    /**
     * 向给定玩家更新实体元数据
     */
    fun updateEntityMetadata(viewer: Player)

    /**
     * 基于给定玩家生成实体元数据
     */
    fun generateEntityMetadata(player: Player): Array<MinecraftMeta>
}