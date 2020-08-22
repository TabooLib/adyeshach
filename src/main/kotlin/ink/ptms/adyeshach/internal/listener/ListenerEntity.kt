package ink.ptms.adyeshach.internal.listener

import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.event.AdyeshachEntityDamageEvent
import ink.ptms.adyeshach.api.event.AdyeshachEntityInteractEvent
import ink.ptms.adyeshach.api.event.AdyeshachEntitySpawnEvent
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityThrowable
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.packet.Packet
import io.izzel.taboolib.module.packet.TPacket
import net.minecraft.server.v1_16_R1.PacketPlayInUseEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
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
        if (packet.`is`("PacketPlayInUseEntity")) {
            val a = packet.read("a", Int::class.java)
            var entity: EntityInstance? = null
            if (entity == null) {
                entity = AdyeshachAPI.getEntityManagerPublic().getEntities().firstOrNull { it.index == a }
            }
            if (entity == null) {
                entity = AdyeshachAPI.getEntityManagerPrivate(player).getEntities().firstOrNull { it.index == a }
            }
            if (entity == null) {
                entity = AdyeshachAPI.getEntityManagerPublicTemporary().getEntities().firstOrNull { it.index == a }
            }
            if (entity == null) {
                entity = AdyeshachAPI.getEntityManagerPrivateTemporary(player).getEntities().firstOrNull { it.index == a }
            }
            if (entity == null) {
                return true
            }
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
}