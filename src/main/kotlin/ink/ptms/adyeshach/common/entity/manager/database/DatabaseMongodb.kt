package ink.ptms.adyeshach.common.entity.manager.database

import ink.ptms.adyeshach.Adyeshach
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.library.configuration.FileConfiguration
import taboolib.module.database.bridge.Index
import taboolib.module.database.bridge.createBridgeCollection

/**
 * @author sky
 * @since 2020-08-14 14:46
 */
class DatabaseMongodb : Database() {

    val collection = createBridgeCollection(
        Adyeshach.conf.getString("Database.url.client"),
        Adyeshach.conf.getString("Database.url.database"),
        Adyeshach.conf.getString("Database.url.collection"),
        Index.UUID
    )

    override fun pull(player: Player): FileConfiguration {
        return collection[adaptPlayer(player)].also {
            if (it.contains("username")) {
                it.set("username", player.name)
            }
        }
    }

    override fun push(player: Player) {
        collection.update(player.uniqueId.toString())
    }

    override fun release(player: Player) {
        collection.release(player.uniqueId.toString())
    }
}