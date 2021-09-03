package ink.ptms.adyeshach.api

import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import java.util.concurrent.ConcurrentHashMap

object HolographicCache {

    val holographicMap = ConcurrentHashMap<String, MutableMap<String, Holographic>>()

    @SubscribeEvent
    fun e(e: PlayerQuitEvent) {
        holographicMap.remove(e.player.name)
    }
}