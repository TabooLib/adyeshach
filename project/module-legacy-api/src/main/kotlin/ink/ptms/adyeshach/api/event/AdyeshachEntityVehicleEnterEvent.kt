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
class AdyeshachEntityVehicleEnterEvent(val entity: EntityInstance, val vehicle: EntityInstance) : BukkitProxyEvent() {

    companion object {

        @SubscribeEvent
        fun legacy(e: ink.ptms.adyeshach.core.event.AdyeshachEntityVehicleEnterEvent) {
            if (!AdyeshachEntityVehicleEnterEvent(EntityTypes.adapt(e.entity) ?: return, EntityTypes.adapt(e.vehicle) ?: return).call()) {
                e.isCancelled = true
            }
        }
    }
}