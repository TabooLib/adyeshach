package ink.ptms.adyeshach.api.event

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author sky
 * @since 2020-08-14 19:21
 */
@Deprecated("Outdated but usable")
class AdyeshachEntityInteractEvent(val entity: EntityInstance, val player: Player, var isMainHand: Boolean, var vector: Vector) : BukkitProxyEvent() {

    companion object {

        @SubscribeEvent
        fun legacy(e: ink.ptms.adyeshach.core.event.AdyeshachEntityInteractEvent) {
            if (!AdyeshachEntityInteractEvent(EntityTypes.adapt(e.entity) ?: return, e.player, e.isMainHand, e.vector).call()) {
                e.isCancelled = true
            }
        }
    }
}