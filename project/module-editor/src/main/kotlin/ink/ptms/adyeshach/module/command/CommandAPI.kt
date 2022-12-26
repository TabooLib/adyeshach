package ink.ptms.adyeshach.module.command

import ink.ptms.adyeshach.module.editor.ChatEditor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.adaptPlayer
import taboolib.expansion.createHelper
import taboolib.platform.util.hasMeta
import taboolib.platform.util.removeMeta
import taboolib.platform.util.setMeta

@CommandHeader(name = "adyeshachapi", aliases = ["aapi"], permission = "adyeshach.command")
object CommandAPI {

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody
    val se = subCommand {
        dynamic("command") {
            execute<Player> { sender, _, args ->
                sender.setMeta("adyeshach_ignore_notice", true)
                try {
                    adaptPlayer(sender).performCommand(args)
                } catch (ex: Throwable) {
                    ex.printStackTrace()
                }
                sender.removeMeta("adyeshach_ignore_notice")
            }
        }
    }

    @CommandBody
    val ee = subCommand {
        dynamic("command") {
            execute<Player> { sender, _, args ->
                sender.setMeta("adyeshach_ignore_notice", true)
                try {
                    adaptPlayer(sender).performCommand(args)
                } catch (ex: Throwable) {
                    ex.printStackTrace()
                }
                ChatEditor.refresh(sender)
                sender.removeMeta("adyeshach_ignore_notice")
            }
        }
    }
}

fun CommandSender.isIgnoreNotice(): Boolean {
    return this is Player && hasMeta("adyeshach_ignore_notice")
}