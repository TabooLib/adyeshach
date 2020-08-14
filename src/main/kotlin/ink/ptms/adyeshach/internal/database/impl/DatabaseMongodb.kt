package ink.ptms.adyeshach.internal.database.impl

import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.internal.database.Database
import io.izzel.taboolib.cronus.bridge.CronusBridge
import io.izzel.taboolib.cronus.bridge.database.IndexType
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-14 14:46
 */
class DatabaseMongodb : Database() {

    val collection = CronusBridge.get(Adyeshach.conf.getString("Database.url.client"), Adyeshach.conf.getString("Database.url.database"), Adyeshach.conf.getString("Database.url.client"), IndexType.UUID)!!

    override fun download(player: Player): FileConfiguration {
        return collection.get(player.uniqueId.toString()).run {
            if (this.contains("username")) {
                this.set("username", player.name)
            }
            this
        }
    }

    override fun upload(player: Player) {
        return collection.update(player.uniqueId.toString())
    }
 }