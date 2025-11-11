package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachSettings
import ink.ptms.adyeshach.core.MinecraftScoreboardOperator
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.type.AdyHuman
import ink.ptms.adyeshach.core.event.AdyeshachEntityVisibleEvent
import ink.ptms.adyeshach.core.event.AdyeshachPlayerJoinVisualTeamEvent
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.PlayerSessionMap
import java.util.concurrent.ConcurrentHashMap

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

    val playerTeams = PlayerSessionMap<PlayerTeam>()

    /**
     * 更新单位的队伍信息
     */
    fun updateTeam(entity: EntityInstance) {
        if (AdyeshachSettings.conf.getString("Settings.team-id", "DISABLED").toString().equals("DISABLED", true)) {
            return
        }
        entity.forViewers { p ->
            val playerTeam = playerTeams.getOrCreate(p) { PlayerTeam(p) }!!
            if (entity.needVisualTeam()) {
                playerTeam.join(entity, entity.isNameTagVisible, entity.isCollision, entity.glowingColor, entity.canSeeInvisible)
            } else {
                playerTeam.leave(entity)
            }
        }
    }

    @SubscribeEvent
    private fun onVisible(e: AdyeshachEntityVisibleEvent) {
        if (e.visible) {
            val playerTeam = playerTeams.getOrCreate(e.viewer) { PlayerTeam(e.viewer) }!!
            if (e.entity.needVisualTeam()) {
                playerTeam.join(e.entity, e.entity.isNameTagVisible, e.entity.isCollision, e.entity.glowingColor, e.entity.canSeeInvisible)
            } else {
                playerTeam.leave(e.entity)
            }
        }
    }

    /**
     * 是否需要虚拟队伍
     */
    private fun EntityInstance.needVisualTeam(): Boolean {
        return !isNameTagVisible || !isCollision || glowingColor != ChatColor.WHITE || canSeeInvisible
    }

    class PlayerTeam(val player: Player) {

        /**
         * 玩家的所有队伍
         */
        val teams = ConcurrentHashMap<String, MinecraftScoreboardOperator.Team>()

        /**
         * 使实体加入玩家的虚拟队伍
         */
        fun join(entity: EntityInstance, nameTagVisible: Boolean, collision: Boolean, color: ChatColor, canSeeInvisible: Boolean) {
            // 离开队伍
            leave(entity)
            // 配置
            if (AdyeshachSettings.conf.getBoolean("Settings.disable-visual-team")) return
            // 事件
            if (!AdyeshachPlayerJoinVisualTeamEvent(player, entity, nameTagVisible, collision, color, canSeeInvisible).call()) return
            // 获取队伍（或创建）
            val team = teams.computeIfAbsent(getKey(nameTagVisible, collision, color, canSeeInvisible)) { key ->
                // 生成队伍名称
                val teamId = AdyeshachSettings.conf.getString("Settings.team-id","ady_{id}")!!.replace("{id}", key)
                // 生成队伍
                val team = MinecraftScoreboardOperator.Team(teamId, hashSetOf(), nameTagVisible, collision, color, canSeeInvisible)
                // 发送队伍数据包
                operator.updateTeam(player, team, MinecraftScoreboardOperator.TeamMethod.ADD)
                team
            }
            // 加入队伍
            val name = if (entity is AdyHuman) entity.getName() else entity.normalizeUniqueId.toString()
            team.members += name
            operator.joinTeam(player, team.name, setOf(name))
            // 如果这个队伍启用了隐形可见
            if (canSeeInvisible) {
                // 把玩家也扔进去
                operator.joinTeam(player, team.name, setOf(player.name))
            }
        }

        /**
         * 使实体离开玩家的虚拟队伍
         */
        fun leave(entity: EntityInstance) {
            val name = if (entity is AdyHuman) entity.getName() else entity.normalizeUniqueId.toString()
            val team = teams.values.firstOrNull { it.members.remove(name) } ?: return
            // 同步数据包
            operator.leaveTeam(player, team.name, setOf(name))
        }
    }

    fun getKey(nameTagVisible: Boolean, collision: Boolean, color: ChatColor, canSeeInvisible: Boolean): String {
        return "${if (nameTagVisible) "T" else "F"}${if (collision) "T" else "F"}${if (canSeeInvisible) "T" else "F"}_${color.ordinal}"
    }
}
