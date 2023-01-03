package ink.ptms.adyeshach.api.event

import org.bukkit.entity.Player
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author sky
 * @since 2020-08-14 19:21
 */
@Deprecated("Outdated but usable")
class AdyeshachPlayerJoinEvent(val player: Player) : BukkitProxyEvent() {

    override val allowCancelled: Boolean
        get() = false

    companion object {

        @SubscribeEvent
        fun legacy(e: ink.ptms.adyeshach.core.event.AdyeshachPlayerJoinEvent) {
            AdyeshachPlayerJoinEvent(e.player).call()
        }
    }
}