package ink.ptms.adyeshach.api.event

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author sky
 * @since 2020-08-14 19:21
 */
@Deprecated("Outdated but usable")
class AdyeshachEntityDestroyEvent(val entity: EntityInstance) : BukkitProxyEvent() {

    companion object {

        @SubscribeEvent
        fun legacy(e: ink.ptms.adyeshach.core.event.AdyeshachEntityDestroyEvent) {
            if (!AdyeshachEntityDestroyEvent(EntityTypes.adapt(e.entity) ?: return).call()) {
                e.isCancelled = true
            }
        }
    }
}