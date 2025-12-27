package ink.ptms.adyeshach.core.event

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class AdyeshachPlayerSetupEvent(val player: Player): BukkitProxyEvent() {

    override val allowCancelled: Boolean
        get() = false
}