package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.common.api.MinecraftMeta
import ink.ptms.adyeshach.common.api.MinecraftMetadataParser
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.MetaMasked
import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.DefaultMetaMasked
 *
 * @author 坏黑
 * @since 2022/6/20 01:32
 */
class DefaultMetaMasked<T : EntityInstance>(index: Int, key: String, mask: Byte, def: Boolean) : MetaMasked<T>(index, key, mask, def) {

    override fun getMetadataParser(): MinecraftMetadataParser<T> {
        TODO("Not yet implemented")
    }

    override fun generateMetadata(player: Player, entityInstance: EntityInstance): MinecraftMeta? {
        TODO("Not yet implemented")
    }

    override fun updateEntityMetadata(entityInstance: EntityInstance) {
        TODO("Not yet implemented")
    }
}