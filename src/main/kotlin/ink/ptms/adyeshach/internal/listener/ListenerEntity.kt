package ink.ptms.adyeshach.internal.listener

import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.event.*
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.util.Tasks
import io.izzel.taboolib.kotlin.Reflex.Companion.reflex
import io.izzel.taboolib.kotlin.Reflex.Companion.reflexInvoke
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.inject.TSchedule
import io.izzel.taboolib.module.packet.Packet
import io.izzel.taboolib.module.packet.TPacket
import io.izzel.taboolib.util.tag.TagDataHandler
import io.izzel.taboolib.util.tag.TagPlayerData
import io.izzel.taboolib.util.tag.TagUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.scoreboard.Team
import org.bukkit.util.Vector

/**
 * @Author sky
 * @Since 2020-08-15 15:53
 */
@TListener
class ListenerEntity : Listener {

    @TPacket(type = TPacket.Type.RECEIVE)
    fun e(player: Player, packet: Packet): Boolean {
        if (packet.`is`("PacketPlayInPosition") && player.name !in AdyeshachAPI.onlinePlayers) {
            AdyeshachAPI.onlinePlayers.add(player.name)
            AdyeshachPlayerJoinEvent(player).call()
        }
        if (packet.`is`("PacketPlayInUseEntity")) {
            val entity = AdyeshachAPI.getEntityFromEntityId(packet.read("a", Int::class.java), player) ?: return true
            // 判定观察者并检测作弊
            if (entity.isViewer(player) && entity.getWorld() == player.world && entity.getLocation().distance(player.location) < 10) {
                when (packet.read("action").toString()) {
                    "ATTACK" -> {
                        Tasks.task {
                            AdyeshachEntityDamageEvent(entity, player).call()
                        }
                    }
                    "INTERACT_AT" -> {
                        val v = packet.read("c")
                        Tasks.task {
                            AdyeshachEntityInteractEvent(
                                entity,
                                player,
                                packet.read("d").toString() == "MAIN_HAND",
                                if (v == null) Vector(0, 0, 0) else NMS.INSTANCE.parseVec3d(v)
                            ).call()
                        }
                    }
                }
            }
        }
        return true
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: AdyeshachEntityTeleportEvent) {
        e.entity.getPassengers().forEach {
            it.teleport(e.location.clone().add(1.5, 0.0, 1.5))
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: AdyeshachNaturalMetaGenerateEvent) {
        val value = e.value
        if (e.meta.key == "customName" && value is String) {
            val length = if (e.entity.entityType == EntityTypes.PLAYER) 46 else 64
            if (value.length > length) {
                e.value = value.substring(0, length)
                println("[Adyeshach] NPC ${e.entity.id} created with name length greater than $length, truncating to ${value.substring(0, length)}")
            }
        }
    }

    @EventHandler
    fun e(e: PlayerJoinEvent) {
        hidePlayers(e.player)
    }

    @TSchedule(period = 200)
    fun e() {
        Bukkit.getOnlinePlayers().forEach {
            hidePlayers(it)
        }
    }

    private fun hidePlayers(player: Player) {
        if (Adyeshach.settings.enableHideName) {
            val scoreboard = TagUtils.getScoreboardComputeIfAbsent(player)
            val data = TagDataHandler.getHandler().getPlayerDataComputeIfAbsent(player)
            val team = TagUtils.getTeamComputeIfAbsent(scoreboard, data.teamHash)
            if (!team.hasEntry("hide_name")) {
                team.addEntry("hide_name")
            }
            val clone = TagPlayerData(player)
            clone.isNameVisibility = false
            clone.nameDisplay = data.nameDisplay
            clone.prefix = data.prefix
            clone.suffix = data.suffix
            TagDataHandler.getHandler().reflexInvoke<Any>("updateTeamVariable", scoreboard, clone)
        }
    }
}