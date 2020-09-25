package ink.ptms.adyeshach.common.entity

import io.izzel.taboolib.module.lite.SimpleCounter
import io.netty.util.internal.ConcurrentSet
import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-14 21:57
 */
class ViewPlayers {

    /**
     * 允许看到这个实体的玩家
     */
    val viewers = ConcurrentSet<String>()

    /**
     * 在这个实体可视范围内的玩家
     */
    val visible = ConcurrentSet<String>()

    val visibleLock = SimpleCounter(20)

    /**
     * 获取允许看到且在可视范围内的所有玩家
     */
    fun getViewPlayers(): List<Player> {
        return Bukkit.getOnlinePlayers().filter { it.name in viewers && it.name in visible }
    }

    /**
     * 获取允许看到但不在可视范围内的所有玩家
     */
    fun getOutsidePlayers(): List<Player> {
        return Bukkit.getOnlinePlayers().filter { it.name in viewers && it.name !in visible }
    }
}