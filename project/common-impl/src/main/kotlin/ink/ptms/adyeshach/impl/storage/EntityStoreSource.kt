package ink.ptms.adyeshach.impl.storage

import org.bukkit.entity.Player
import taboolib.library.configuration.ConfigurationSection

/**
 * @author sky
 * @since 2020-08-14 14:38
 */
@Deprecated("1.0 保留功能")
abstract class EntityStoreSource {

    abstract fun get(player: Player): ConfigurationSection

    abstract fun update(player: Player)

    abstract fun release(player: Player)
}