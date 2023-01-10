package ink.ptms.adyeshach.impl.storage

import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.library.configuration.ConfigurationSection

/**
 * @author sky
 * @since 2020-08-14 14:46
 */
@Deprecated("1.0 保留功能")
class EntityStoreSourceMongoDB : EntityStoreSource() {

    val collection = createBridgeCollection(
        EntityStorage.databaseUrl!!.getString("client")!!,
        EntityStorage.databaseUrl!!.getString("database")!!,
        EntityStorage.databaseUrl!!.getString("collection")!!,
        Index.UUID
    )

    override fun get(player: Player): ConfigurationSection {
        return collection[adaptPlayer(player)].also {
            if (it.contains("username")) {
                it["username"] = player.name
            }
        }
    }

    override fun update(player: Player) {
        collection.update(player.uniqueId.toString())
    }

    override fun release(player: Player) {
        collection.release(player.uniqueId.toString())
    }
}