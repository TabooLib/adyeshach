package ink.ptms.adyeshach.common.util

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

    fun nextIndex(): Int {
        return nextIndex("@public")
    }

    fun nextIndex(name: String): Int {
        return index.put(name, index.computeIfAbsent(name) { 449599702 } + 1)!!
    }
}