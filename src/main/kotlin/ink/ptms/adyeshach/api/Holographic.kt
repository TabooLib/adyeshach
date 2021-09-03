package ink.ptms.adyeshach.api

import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import taboolib.common5.util.printed

class Holographic(val player: Player, val location: Location, val message: List<String>, val transfer: (String) -> (String)) {

    val holograms = ArrayList<Hologram<*>>()
    val time = System.currentTimeMillis()

    init {
        message.forEachIndexed { index, content ->
            val hologram = AdyeshachAPI.createHologram(player, location.clone().add(0.0, (((message.size - 1) - index) * 0.3), 0.0), listOf(content))
            val map = content.printed("_").map(transfer)
            map.forEachIndexed { i, s ->
                submit(delay = i.toLong()) {
                    hologram.update(listOf(s))
                }
            }
            holograms += hologram
        }
    }

    fun cancel() {
        holograms.forEach { it.delete() }
    }
}