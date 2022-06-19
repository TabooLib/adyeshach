package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.common.api.MinecraftMeta
import ink.ptms.adyeshach.common.entity.Meta
import ink.ptms.adyeshach.common.entity.Metaable
import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.internal.entity.DefaultMetaable
 *
 * @author 坏黑
 * @since 2022/6/19 16:11
 */
@Suppress("SpellCheckingInspection")
interface DefaultMetaable : Metaable {

    override fun <T> getMetadata(key: String): T {
        TODO("Not yet implemented")
    }

    override fun setMetadata(key: String, value: Any): Boolean {
        TODO("Not yet implemented")
    }

    override fun getAvailableEntityMeta(): List<Meta<*>> {
        TODO("Not yet implemented")
    }

    override fun updateEntityMetadata() {
        TODO("Not yet implemented")
    }

    override fun updateEntityMetadata(viewer: Player) {
        TODO("Not yet implemented")
    }

    override fun generateEntityMetadata(player: Player): Array<MinecraftMeta> {
        TODO("Not yet implemented")
    }
}