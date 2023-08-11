package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.MinecraftScoreboardOperator
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.type.AdyHuman
import ink.ptms.adyeshach.core.event.AdyeshachEntityVisibleEvent
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import java.util.concurrent.ConcurrentHashMap
import ink.ptms.adyeshach.core.AdyeshachSettings

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.VisualTeam
 *
 * @author 坏黑
 * @since 2023/1/10 02:50
 */
object VisualTeam {

    val operator: MinecraftScoreboardOperator
        get() = Adyeshach.api().getMinecraftAPI().getScoreboardOperator()

    val playerTeams = ConcurrentHashMap<String, PlayerTeam>()

    /**
     * 更新单位的队伍信息
     */
    fun updateTeam(entity: EntityInstance) {
        if (AdyeshachSettings.conf.getString("Settings.team-id", "DISABLED").toString().equals("DISABLED", true)) {
            return
        }
        entity.forViewers { p ->
            val playerTeam = playerTeams.computeIfAbsent(p.name) { PlayerTeam(p) }
            if (entity.needVisualTeam()) {
                playerTeam.join(entity, entity.isNameTagVisible, entity.isCollision, entity.glowingColor)
            } else {
                playerTeam.leaveAll(entity)
            }
        }
    }

    @SubscribeEvent
    private fun onVisible(e: AdyeshachEntityVisibleEvent) {
        if (e.visible) {
            val playerTeam = playerTeams.computeIfAbsent(e.viewer.name) { PlayerTeam(e.viewer) }
            if (e.entity.needVisualTeam()) {
                playerTeam.join(e.entity, e.entity.isNameTagVisible, e.entity.isCollision, e.entity.glowingColor)
            }
        } else {
            // 不管怎么样都会从队伍中移除
            playerTeams[e.viewer.name]?.leaveAll(e.entity)
        }
    }

    @SubscribeEvent
    private fun onQuit(e: PlayerQuitEvent) {
        playerTeams.remove(e.player.name)
    }

    private fun EntityInstance.needVisualTeam(): Boolean {
        return !isNameTagVisible || !isCollision || glowingColor != ChatColor.WHITE
    }

    class PlayerTeam(val player: Player) {

        val teams = ConcurrentHashMap<String, MinecraftScoreboardOperator.Team>()

        fun join(entity: EntityInstance, nameTagVisible: Boolean = true, collision: Boolean = true, color: ChatColor = ChatColor.WHITE) {
            // 离开队伍
            leaveAll(entity)
            // 获取队伍（或创建）
            val team = teams.computeIfAbsent(getKey(nameTagVisible, collision, color)) {
                val id = AdyeshachSettings.conf.getString("Settings.team-id","ady_{id}")!!.replace("{id}",it)
                MinecraftScoreboardOperator.Team(id, hashSetOf(), nameTagVisible, collision, color)
            }
            // 新的队伍
            if (team.members.isEmpty()) {
                operator.updateTeam(player, team, MinecraftScoreboardOperator.TeamMethod.ADD)
            }
            // 加入队伍
            val name = if (entity is AdyHuman) entity.getName() else entity.normalizeUniqueId.toString()
            team.members += name
            operator.joinTeam(player, team.name, setOf(name))
        }

        fun leaveAll(entity: EntityInstance) {
            val name = if (entity is AdyHuman) entity.getName() else entity.normalizeUniqueId.toString()
            val team = teams.values.firstOrNull { it.members.remove(name) } ?: return
            // 同步数据包
            if (team.members.isNotEmpty()) {
                operator.leaveTeam(player, team.name, setOf(name))
            } else {
                operator.removeTeam(player, team.name)
            }
        }
    }

    fun getKey(nameTagVisible: Boolean, collision: Boolean, color: ChatColor): String {
        return "${if (nameTagVisible) "1" else "0"}${if (collision) "1" else "0"}${color.ordinal}"
    }
}
