package ink.ptms.adyeshach.core.entity

import org.bukkit.entity.Player
import taboolib.common5.Baffle
import java.util.function.Function

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.ViewPlayers
 *
 * @author 坏黑
 * @since 2022/6/15 23:31
 */
interface ViewPlayers {

    /**
     * 允许看到这个实体的玩家
     */
    val viewers: MutableSet<String>

    /**
     * 在这个实体可视范围内的玩家
     */
    val visible: MutableSet<String>

    /**
     * 获取允许看到的所有玩家
     */
    fun getPlayers(): List<Player>

    /**
     * 获取允许看到的且在可视范围内所有玩家（未看到的）
     */
    fun getPlayersInViewDistance(): List<Player>

    /**
     * 获取允许看到且在可视范围内的所有玩家（已经看到的）
     */
    fun getViewPlayers(): List<Player>

    /**
     * 获取允许看到且在可视范围内的所有玩家（已经看到的）
     */
    fun getViewPlayers(cond: Function<Player, Boolean>): List<Player>

    /**
     * 获取允许看到但不在可视范围内的所有玩家
     */
    fun getOutsidePlayers(): List<Player>

    /**
     * 获取允许看到但不在可视范围内的所有玩家
     */
    fun getOutsidePlayers(cond: Function<Player, Boolean>): List<Player>

    /**
     * 是否存在任何可见玩家
     */
    fun hasVisiblePlayer(): Boolean
}