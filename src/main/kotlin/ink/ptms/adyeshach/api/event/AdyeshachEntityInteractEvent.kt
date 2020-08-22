package ink.ptms.adyeshach.api.event

import ink.ptms.adyeshach.common.entity.EntityInstance
import io.izzel.taboolib.module.event.EventCancellable
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector

/**
 * @Author sky
 * @Since 2020-08-14 19:21
 */
class AdyeshachEntityInteractEvent(val entity: EntityInstance, var player: Player, var isMainHand: Boolean, var vector: Vector) : EventCancellable<AdyeshachEntityInteractEvent>() {

    init {
        async(!Bukkit.isPrimaryThread())
    }
}