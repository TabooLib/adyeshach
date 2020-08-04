package ink.ptms.adyeshach.common.util

import com.google.common.collect.Maps
import io.izzel.taboolib.module.hologram.THologram
import io.izzel.taboolib.module.inject.PlayerContainer
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap

/**
 * @Author sky
 * @Since 2020-08-04 13:00
 */
object Indexs {

    @PlayerContainer
    private val index = ConcurrentHashMap<String, Int>()

    fun nextIndex(player: Player): Int {
        return index.put(player.name, index.computeIfAbsent(player.name) { 449599702 } + 1)!!
    }
}