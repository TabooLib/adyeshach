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
     * 创建虚拟队伍
     *
     * @param player 数据包接收人
     * @param name 队伍名称
     * @param members 队伍成员
     * @param nameTagVisible 名称可见
     * @param collision 是否碰撞
     * @param color 队伍颜色
     */
    fun createTeam(
        player: List<Player>,
        name: String,
        members: Set<String>,
        nameTagVisible: Boolean = true,
        collision: Boolean = true,
        color: ChatColor = ChatColor.WHITE
    ) {
        updateTeam(player, Team(name, members.toMutableSet(), nameTagVisible, collision, color), TeamMethod.ADD)
    }

    fun createTeam(
        player: Player,
        name: String,
        members: Set<String>,
        nameTagVisible: Boolean = true,
        collision: Boolean = true,
        color: ChatColor = ChatColor.WHITE
    ) {
        updateTeam(listOf(player), Team(name, members.toMutableSet(), nameTagVisible, collision, color), TeamMethod.ADD)
    }

    /**
     * 使实体加入虚拟队伍
     *
     * @param player 数据包接收人
     * @param name 队伍名称
     * @param members 队伍成员
     */
    fun joinTeam(player: List<Player>, name: String, members: Set<String>) {
        updateTeam(player, Team(name, members.toMutableSet()), TeamMethod.JOIN)
    }

    fun joinTeam(player: Player, name: String, members: Set<String>) {
        updateTeam(listOf(player), Team(name, members.toMutableSet()), TeamMethod.JOIN)
    }

    /**
     * 使实体离开虚拟队伍
     *
     * @param player 数据包接收人
     * @param name 队伍名称
     * @param members 队伍成员
     */
    fun leaveTeam(player: List<Player>, name: String, members: Set<String>) {
        updateTeam(player, Team(name, members.toMutableSet()), TeamMethod.LEAVE)
    }

    fun leaveTeam(player: Player, name: String, members: Set<String>) {
        updateTeam(listOf(player), Team(name, members.toMutableSet()), TeamMethod.LEAVE)
    }

    /**
     * 删除虚拟队伍
     *
     * @param player 数据包接收人
     * @param name 队伍名称
     */
    fun removeTeam(player: List<Player>, name: String) {
        updateTeam(player, Team(name), TeamMethod.REMOVE)
    }

    fun removeTeam(player: Player, name: String) {
        updateTeam(listOf(player), Team(name), TeamMethod.REMOVE)
    }

    /**
     * 修改虚拟队伍信息
     *
     * @param player 数据包接收人
     * @param name 队伍名称
     * @param nameTagVisible 名称可见
     * @param collision 是否碰撞
     * @param color 队伍颜色
     */
    fun modifyTeam(player: Player, name: String, nameTagVisible: Boolean = true, collision: Boolean = true, color: ChatColor = ChatColor.WHITE) {
        updateTeam(listOf(player), Team(name, nameTagVisible = nameTagVisible, collision = collision, color = color), TeamMethod.CHANGE)
    }

    fun modifyTeam(player: List<Player>, name: String, nameTagVisible: Boolean = true, collision: Boolean = true, color: ChatColor = ChatColor.WHITE) {
        updateTeam(player, Team(name, nameTagVisible = nameTagVisible, collision = collision, color = color), TeamMethod.CHANGE)
    }

    /**
     * 队伍信息
     */
    data class Team(
        var name: String,
        var members: MutableSet<String> = hashSetOf(),
        var nameTagVisible: Boolean = true,
        var collision: Boolean = true,
        var color: ChatColor = ChatColor.WHITE
    ) {

        override fun toString(): String {
            return "Team(name='$name', members=$members, nameTagVisible=$nameTagVisible, collision=$collision, color=&${color.char})"
        }
    }

    /**
     * 队伍控制方法
     */
    enum class TeamMethod {

        ADD, REMOVE, CHANGE, JOIN, LEAVE
    }
}