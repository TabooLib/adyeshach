package ink.ptms.adyeshach.api.event

import ink.ptms.adyeshach.common.entity.EntityMetaable
import ink.ptms.adyeshach.common.entity.EntityTypes
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author sky
 * @since 2020-08-14 19:21
 */
@Deprecated("Outdated but usable")
class AdyeshachTagUpdateEvent(val entity: EntityMetaable, val key: String, var value: Any?) : BukkitProxyEvent() {

    companion object {

        @SubscribeEvent
        fun legacy(e: ink.ptms.adyeshach.core.event.AdyeshachTagUpdateEvent) {
            val event = AdyeshachTagUpdateEvent(EntityTypes.adapt(e.entity) ?: return, e.key, e.value)
            if (event.call()) {
                e.value = event.value
            } else {
                e.isCancelled = true
            }
        }
    }
}