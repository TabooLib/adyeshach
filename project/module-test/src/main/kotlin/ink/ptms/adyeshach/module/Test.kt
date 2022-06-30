package ink.ptms.adyeshach.module

import org.bukkit.command.CommandSender
import org.bukkit.event.player.PlayerJoinEvent
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.module.nms.PacketSendEvent

@CommandHeader(name = "adytest")
object Test {

    @CommandBody
    val test1 = subCommand {
        execute<CommandSender> { sender, _, _ ->
            sender.info("MinecraftEntityMetadataHandler")
            testParser(sender)
            testCreateMeta(sender)
            sender.info("MinecraftHelper")
            testAdapter(sender)
        }
    }

//    var entityId = -1
//
//    @SubscribeEvent
//    fun e(e: PlayerJoinEvent) {
//        entityId = e.player.entityId
//    }
//
//    @SubscribeEvent
//    fun e(e: PacketSendEvent) {
//        kotlin.runCatching {
//            if (e.packet.read<Int>("a")!! == entityId && e.player.entityId != entityId) {
//                info("Packet: ${e.packet.name}")
//            }
//        }
//    }
}