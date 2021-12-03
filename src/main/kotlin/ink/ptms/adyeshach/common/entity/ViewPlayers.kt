package ink.ptms.adyeshach.common.entity

import io.netty.util.internal.ConcurrentSet
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common5.Baffle

/**
 * @author sky
 * @since 2020-08-14 21:57
 */
open class ViewPlayers(val entityInstance: EntityInstance) {

    /**
     * 允许看到这个实体的玩家
     */
    val viewers = ConcurrentSet<String>()

    /**
     * 在这个实体可视范围内的玩家
     */
    val visible = ConcurrentSet<String>()

    /**
     * 用于控制单位显示距离的阻断器
     */
    val visibleRefreshLocker = Baffle.of(20)

    /**
     * 获取允许看到的所有玩家
     */
    fun getPlayers(): List<Player> {
        return Bukkit.getOnlinePlayers().filter { it.name in viewers }
    }

    /**
     * 获取允许看到的且在可视范围内所有玩家（未看到的）
     */
    fun getPlayersInViewDistance(): List<Player> {
        return Bukkit.getOnlinePlayers().filter { it.name in viewers && entityInstance.isInVisibleDistance(it) }
    }

    /**
     * 获取允许看到且在可视范围内的所有玩家（已经到的）
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