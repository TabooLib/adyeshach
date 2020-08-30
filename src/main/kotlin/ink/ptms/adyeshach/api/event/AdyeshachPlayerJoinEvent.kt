package ink.ptms.adyeshach.api.event

import io.izzel.taboolib.module.event.EventNormal
import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-14 19:21
 */
class AdyeshachPlayerJoinEvent(val player: Player) : EventNormal<AdyeshachPlayerJoinEvent>() {

    init {
        async(!Bukkit.isPrimaryThread())
    }
}