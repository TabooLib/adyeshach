package ink.ptms.adyeshach

import io.izzel.taboolib.module.command.lite.CommandBuilder
import io.izzel.taboolib.module.inject.TInject
import io.izzel.taboolib.module.packet.Packet
import io.izzel.taboolib.module.packet.TPacket
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.util.Vector

/**
 * @author Arasple
 * @date 2020/8/3 22:34
 * Player (NPC) test
 */
@Suppress("UNCHECKED_CAST")
object TestP {

    var entityId = -1

    @TPacket(type = TPacket.Type.SEND)
    fun e(player: Player, packet: Packet): Boolean {
        if (packet.read("a") == entityId) {
            println(packet.get().javaClass.simpleName)
        }
        return true
    }

    @TInject
    val testV: CommandBuilder = CommandBuilder
            .create("test-p", Adyeshach.plugin)
            .execute { sender, _ ->
                if (sender is Player) {
                    val e = sender.world.spawn(sender.location, Villager::class.java)
                    entityId = e.entityId
                    e.velocity = Vector(0, 10, 0)


                    sender.sendMessage("§c[System] §7Done.")
                }
            }
}