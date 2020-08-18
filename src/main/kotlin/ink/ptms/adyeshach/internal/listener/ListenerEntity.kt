package ink.ptms.adyeshach.internal.listener

import ink.ptms.adyeshach.api.event.AdyeshachEntitySpawnEvent
import ink.ptms.adyeshach.common.entity.EntityThrowable
import io.izzel.taboolib.module.inject.TListener
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

/**
 * @Author sky
 * @Since 2020-08-15 15:53
 */
@TListener
class ListenerEntity : Listener {

    @EventHandler
    fun e(e: AdyeshachEntitySpawnEvent) {
        if (e.entity is EntityThrowable) {
            e.entity.setNoGravity(true)
        }
    }
}