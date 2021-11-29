package ink.ptms.adyeshach.common.entity.manager.database

import org.bukkit.entity.Player
import taboolib.library.configuration.ConfigurationSection

/**
 * @author sky
 * @since 2020-08-14 14:38
 */
abstract class Database {

    abstract fun pull(player: Player): ConfigurationSection

    abstract fun push(player: Player)

    abstract fun release(player: Player)

}