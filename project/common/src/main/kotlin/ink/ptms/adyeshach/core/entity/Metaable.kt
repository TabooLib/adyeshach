package ink.ptms.adyeshach.core.entity

import ink.ptms.adyeshach.core.MinecraftMeta
import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.Metaable
 *
 * @author 坏黑
 * @since 2022/6/15 23:04
 */
@Suppress("SpellCheckingInspection")
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
     * 设置实体元数据（通过字符串设置并自动解析为对应的值）
     */
    fun setCustomMeta(key: String, value: String): Boolean

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