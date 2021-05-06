package ink.ptms.adyeshach.internal.listener

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.event.*
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.util.Tasks
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.module.packet.Packet
import io.izzel.taboolib.module.packet.TPacket
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
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
                            AdyeshachEntityInteractEvent(entity, player, packet.read("d").toString() == "MAIN_HAND", if (v == null) Vector(0, 0, 0) else NMS.INSTANCE.parseVec3d(v)).call()
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
}