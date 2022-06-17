package ink.ptms.adyeshach.common.entity

import ink.ptms.adyeshach.common.api.MinecraftMeta
import ink.ptms.adyeshach.common.api.MinecraftMetadataParser
import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.entity.Meta
 *
 * @author 坏黑
 * @since 2022/6/15 23:07
 */
abstract class Meta<T : EntityInstance>(val index: Int, val key: String, val def: Any) {

    /**
     * 获取元数据鉴别器
     */
    abstract fun getMetadataParser(): MinecraftMetadataParser<T>

    /**
     * 生成实体元数据
     */
    abstract fun generateMetadata(player: Player, entityInstance: EntityInstance): MinecraftMeta?

    /**
     * 向所有观察者更新元数据信息
     */
    abstract fun updateEntityMetadata(entityInstance: EntityInstance)
}