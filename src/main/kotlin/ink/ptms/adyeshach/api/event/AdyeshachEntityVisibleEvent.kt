package ink.ptms.adyeshach.api.event

import ink.ptms.adyeshach.common.entity.EntityInstance
import io.izzel.taboolib.module.event.EventCancellable
import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-14 19:21
 */
class AdyeshachEntityVisibleEvent(val entity: EntityInstance, val viewer: Player, val visible: Boolean) : EventCancellable<AdyeshachEntityVisibleEvent>() {

    init {
        async(!Bukkit.isPrimaryThread())
    }
}