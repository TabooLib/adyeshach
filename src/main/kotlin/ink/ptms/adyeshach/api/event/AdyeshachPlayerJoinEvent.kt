package ink.ptms.adyeshach.api.event

import ink.ptms.adyeshach.common.entity.EntityInstance
import io.izzel.taboolib.module.event.EventCancellable
import io.izzel.taboolib.module.event.EventNormal
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.util.Vector

/**
 * @Author sky
 * @Since 2020-08-14 19:21
 */
class AdyeshachPlayerJoinEvent(val player: Player) : EventNormal<AdyeshachPlayerJoinEvent>() {

    init {
        async(!Bukkit.isPrimaryThread())
    }
}