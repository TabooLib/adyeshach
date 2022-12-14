package ink.ptms.adyeshach.core.event

import ink.ptms.adyeshach.core.bukkit.data.GameProfile
import ink.ptms.adyeshach.core.entity.type.AdyHuman
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author sky
 * @since 2020-08-14 19:21
 */
class AdyeshachGameProfileGenerateEvent(val entity: AdyHuman, val player: Player, var gameProfile: GameProfile) : BukkitProxyEvent() {

    override val allowCancelled: Boolean
        get() = false
}