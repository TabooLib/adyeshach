package ink.ptms.adyeshach.core.entity

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.Meta
 *
 * @author 坏黑
 * @since 2022/6/15 23:07
 */
abstract class Meta<T : EntityInstance>(val index: Int, val key: String, val group: String, val def: Any) {

    /**
     * 获取元数据鉴别器
     */
    abstract fun getMetadataParser(): MinecraftMetadataParser<Any>

    /**
     * 生成实体元数据
     * 会触发对应的事件，例如：
     * + MetaMaskedGenerateEvent
     * + MetaNaturalGenerateEvent
     */
    abstract fun generateMetadata(player: Player, entityInstance: EntityInstance): MinecraftMeta?

    /**
     * 生成实体元数据（开发用函数）
     * 不会触发任何事件
     */
    abstract fun generateMetadata(entityInstance: EntityInstance): MinecraftMeta

    /**
     * 向所有观察者更新元数据信息
     */
    abstract fun updateEntityMetadata(entityInstance: EntityInstance)
}

/** 是否为布尔值类型的元数据 */
fun Meta<*>.isBool() = def is Boolean || this is MetaMasked<*>