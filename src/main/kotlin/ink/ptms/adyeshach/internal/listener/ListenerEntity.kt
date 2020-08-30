package ink.ptms.adyeshach.internal.listener

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.event.*
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.EntityThrowable
import ink.ptms.adyeshach.common.entity.type.AdyEntityLiving
import ink.ptms.adyeshach.common.util.Tasks
import io.izzel.taboolib.module.inject.TListener
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
            if (packet.read("action").toString() == "ATTACK") {
                AdyeshachEntityDamageEvent(entity, player).call()
            } else {
                val v = packet.read("c")
                AdyeshachEntityInteractEvent(entity, player, packet.read("d").toString() == "MAIN_HAND", if (v == null) Vector(0, 0, 0) else NMS.INSTANCE.parseVec3d(v)).call()
            }
        }
        return true
    }

    @EventHandler
    fun e(e: AdyeshachEntitySpawnEvent) {
        if (e.entity is EntityThrowable) {
            e.entity.setNoGravity(true)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: AdyeshachEntityVisibleEvent) {
        if (e.visible && e.entity is AdyEntityLiving) {
            Tasks.task {
                e.entity.updateEquipment()
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: AdyeshachEntityTeleportEvent) {
        e.entity.getPassengers().forEach {
            it.teleport(e.location.clone().add(1.5, 0.0, 1.5))
        }
    }
}