package ink.ptms.adyeshach.internal.database

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-14 14:38
 */
abstract class Database {

    abstract fun download(player: Player): FileConfiguration

    abstract fun upload(player: Player)

}