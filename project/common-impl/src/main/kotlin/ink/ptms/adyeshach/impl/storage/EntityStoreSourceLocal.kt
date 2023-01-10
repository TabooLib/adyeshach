package ink.ptms.adyeshach.impl.storage

import org.bukkit.entity.Player
import taboolib.common.platform.function.getDataFolder
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration
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
@Deprecated("1.0 保留功能")
class EntityStoreSourceLocal : EntityStoreSource() {

    val host = File(getDataFolder(), "data.db").getHost()

    val table = Table("npc", host) {
        add {
            name("user")
            type(ColumnTypeSQLite.TEXT, 36) {
                options(ColumnOptionSQLite.PRIMARY_KEY)
            }
        }
        add {
            name("data")
            type(ColumnTypeSQLite.TEXT)
        }
    }

    val dataSource = host.createDataSource()
    val cache = ConcurrentHashMap<String, Configuration>()

    init {
        table.createTable(dataSource)
    }

    override fun get(player: Player): ConfigurationSection {
        return cache.computeIfAbsent(player.name) {
            table.select(dataSource) {
                where { "user" eq player.name }
            }.firstOrNull {
                Configuration.loadFromString(getString("data"))
            } ?: Configuration.empty(Type.YAML)
        }
    }

    override fun update(player: Player) {
        val file = cache[player.name] ?: return
        if (table.find(dataSource) { where { "user" eq player.name } }) {
            table.update(dataSource) {
                set("data", file.saveToString())
                where { "user" eq player.name }
            }
        } else {
            table.insert(dataSource, "user", "data") {
                value(player.name, file.saveToString())
            }
        }
    }

    override fun release(player: Player) {
        cache.remove(player.name)
    }
}