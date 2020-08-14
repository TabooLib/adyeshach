package ink.ptms.adyeshach.internal.database

import io.izzel.taboolib.module.inject.TFunction
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-14 14:38
 */
abstract class Database {

    abstract fun download(player: Player): FileConfiguration

    abstract fun upload(player: Player)

}