package ink.ptms.adyeshach.api.event

import ink.ptms.adyeshach.common.bukkit.data.GameProfile
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.type.AdyHuman
import org.bukkit.entity.Player
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author sky
 * @since 2020-08-14 19:21
 */
@Deprecated("Outdated but usable")
class AdyeshachGameProfileGenerateEvent(val entity: AdyHuman, val player: Player, var gameProfile: GameProfile) : BukkitProxyEvent() {

    override val allowCancelled: Boolean
        get() = false

    companion object {

        @SubscribeEvent
        fun legacy(e: ink.ptms.adyeshach.core.event.AdyeshachGameProfileGenerateEvent) {
            AdyeshachGameProfileGenerateEvent(EntityTypes.adapt(e.entity) as AdyHuman, e.player, GameProfile(e.gameProfile)).call()
        }
    }
}