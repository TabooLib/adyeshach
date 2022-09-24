package ink.ptms.adyeshach.internal.listener

import com.melluh.servertours.api.event.RoutePlaybackBeginEvent
import com.melluh.servertours.api.event.RoutePlaybackEndEvent
import io.netty.util.internal.ConcurrentSet
import taboolib.common.platform.event.SubscribeEvent

object ListenerServerTour {

    var touringPlayer = ConcurrentSet<String>()

    @SubscribeEvent
    fun e(e: RoutePlaybackBeginEvent) {
        touringPlayer.add(e.bukkitPlayer.name)
    }

    @SubscribeEvent
    fun e(e: RoutePlaybackEndEvent) {
        touringPlayer.remove(e.bukkitPlayer.name)
    }
}