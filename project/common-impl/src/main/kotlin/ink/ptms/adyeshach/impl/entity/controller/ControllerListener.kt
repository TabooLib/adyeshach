package ink.ptms.adyeshach.impl.entity.controller

import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent

object ControllerListener {

    @SubscribeEvent
    private fun onQuit(e: PlayerQuitEvent) {
        ControllerLookAtPlayerWithPacket.LAST_LOOK_ANGLES.remove(e.player.name)
    }
}