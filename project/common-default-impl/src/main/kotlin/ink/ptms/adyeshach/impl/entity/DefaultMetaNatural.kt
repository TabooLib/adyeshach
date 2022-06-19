package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.common.api.MinecraftMeta
import ink.ptms.adyeshach.common.api.MinecraftMetadataParser
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.MetaNatural
import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.DefaultMetaNatural
 *
 * @author 坏黑
 * @since 2022/6/20 01:33
 */
class DefaultMetaNatural<T, E : EntityInstance>(index: Int, key: String, def: T) : MetaNatural<T, E>(index, key, def) {

    override fun getMetadataParser(): MinecraftMetadataParser<E> {
        TODO("Not yet implemented")
    }

    override fun generateMetadata(player: Player, entityInstance: EntityInstance): MinecraftMeta? {
        TODO("Not yet implemented")
    }

    override fun updateEntityMetadata(entityInstance: EntityInstance) {
        TODO("Not yet implemented")
    }
}