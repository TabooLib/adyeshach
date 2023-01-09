package ink.ptms.adyeshach.core

import org.bukkit.ChatColor
import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.MinecraftScoreboardOperator
 *
 * @author 坏黑
 * @since 2023/1/10 01:43
 */
interface MinecraftScoreboardOperator {

    /**
     * 发送虚拟队伍信息
     *
     * @param player 数据包接收人
     * @param team 队伍信息
     * @param method 队伍控制方法
     */
    fun updateTeam(player: List<Player>, team: Team, method: TeamMethod)
    fun updateTeam(player: Player, team: Team, method: TeamMethod) {
        updateTeam(listOf(player), team, method)
    }

    /**
     * 队伍信息
     */
    data class Team(
        var name: String,
        var members: ArrayList<String> = arrayListOf(),
        var nameTagVisible: Boolean = true,
        var collision: Boolean = true,
        var color: ChatColor = ChatColor.WHITE
    )

    /**
     * 队伍控制方法
     */
    enum class TeamMethod {

        ADD, REMOVE, CHANGE, JOIN, LEAVE
    }
}