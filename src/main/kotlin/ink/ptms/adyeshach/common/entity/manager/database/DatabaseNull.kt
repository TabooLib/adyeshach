package ink.ptms.adyeshach.common.entity.manager.database

import org.bukkit.entity.Player
import taboolib.common.platform.function.getDataFolder
import taboolib.module.configuration.Configuration
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Type
import taboolib.module.database.ColumnOptionSQLite
import taboolib.module.database.ColumnTypeSQLite
import taboolib.module.database.Table
import taboolib.module.database.getHost
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * @author sky
 * @since 2020-08-14 14:46
 */
class DatabaseNull : Database() {

    override fun pull(player: Player): ConfigurationSection {
        return Configuration.empty(Type.YAML)
    }

    override fun push(player: Player) {
    }

    override fun release(player: Player) {
    }
}