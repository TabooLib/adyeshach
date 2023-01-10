package ink.ptms.adyeshach.impl.storage

import org.bukkit.entity.Player
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type

/**
 * @author sky
 * @since 2020-08-14 14:46
 */
@Deprecated("1.0 保留功能")
class EntityStoreSourceNull : EntityStoreSource() {

    override fun get(player: Player): ConfigurationSection {
        return Configuration.empty(Type.YAML)
    }

    override fun update(player: Player) {
    }

    override fun release(player: Player) {
    }
}