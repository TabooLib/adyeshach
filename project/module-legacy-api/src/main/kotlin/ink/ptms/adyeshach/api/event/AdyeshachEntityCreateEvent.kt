package ink.ptms.adyeshach.api.event

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.Location
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author sky
 * @since 2020-08-14 19:21
 */
@Deprecated("Outdated but usable")
class AdyeshachEntityCreateEvent(val entity: EntityInstance, var location: Location) : BukkitProxyEvent() {

    companion object {

        @SubscribeEvent
        fun legacy(e: ink.ptms.adyeshach.core.event.AdyeshachEntityCreateEvent) {
            if (!AdyeshachEntityCreateEvent(EntityTypes.adapt(e.entity) ?: return, e.location).call()) {
                e.isCancelled = true
            }
        }
    }
}