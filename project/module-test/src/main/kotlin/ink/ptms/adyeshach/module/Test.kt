package ink.ptms.adyeshach.module

import org.bukkit.command.CommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.subCommand

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
}