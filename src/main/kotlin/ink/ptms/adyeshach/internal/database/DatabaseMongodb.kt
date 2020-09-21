package ink.ptms.adyeshach.internal.database

import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.common.util.Tasks
import io.izzel.taboolib.cronus.bridge.CronusBridge
import io.izzel.taboolib.cronus.bridge.database.IndexType
import io.izzel.taboolib.module.nms.NMS
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-14 14:46
 */
class DatabaseMongodb : Database() {

    val collection = CronusBridge.get(Adyeshach.conf.getString("Database.url.client"), Adyeshach.conf.getString("Database.url.database"), Adyeshach.conf.getString("Database.url.collection"), IndexType.UUID)!!

    override fun download(player: Player): FileConfiguration {
        return collection.get(player.uniqueId.toString()).run {
            if (this.contains("username")) {
                this.set("username", player.name)
            }
            this
        }
    }

    override fun upload(player: Player) {
        if (NMS.handle().isRunning) {
            Tasks.task(true) {
                collection.update(player.uniqueId.toString())
            }
        } else {
            collection.update(player.uniqueId.toString())
        }
    }
 }