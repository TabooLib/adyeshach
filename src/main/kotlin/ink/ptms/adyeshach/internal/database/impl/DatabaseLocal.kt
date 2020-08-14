package ink.ptms.adyeshach.internal.database.impl

import ink.ptms.adyeshach.internal.database.Database
import io.izzel.taboolib.module.db.local.LocalPlayer
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-14 14:46
 */
class DatabaseLocal : Database() {

    override fun download(player: Player): FileConfiguration {
        return LocalPlayer.get(player)
    }

    override fun upload(player: Player) {
    }

}