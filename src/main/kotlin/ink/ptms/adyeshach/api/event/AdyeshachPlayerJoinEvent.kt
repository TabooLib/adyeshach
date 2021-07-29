package ink.ptms.adyeshach.api.event

import org.bukkit.entity.Player
import taboolib.common.platform.ProxyEvent

/**
 * @Author sky
 * @Since 2020-08-14 19:21
 */
class AdyeshachPlayerJoinEvent(val player: Player) : ProxyEvent() {

    override val allowCancelled: Boolean
        get() = false
}