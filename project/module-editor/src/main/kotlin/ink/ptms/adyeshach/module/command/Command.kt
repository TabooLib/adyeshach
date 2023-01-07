package ink.ptms.adyeshach.module.command

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachSettings
import ink.ptms.adyeshach.module.command.subcommand.*
import org.bukkit.command.CommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.common.util.ResettableLazy
import taboolib.common.util.unsafeLazy
import taboolib.expansion.createHelper

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.command.Command
 *
 * @author 坏黑
 * @since 2022/12/17 20:47
 */
@CommandHeader(name = "adyeshach", aliases = ["ady", "npc"], permission = "adyeshach.command")
object Command {

    val finder by unsafeLazy { Adyeshach.api().getEntityFinder() }

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody
    val script = CommandScript

    @CommandBody
    val api = CommandAPI

    @CommandBody
    val create = createSubCommand

    @CommandBody
    val remove = removeSubCommand

    @CommandBody
    val rename = renameSubCommand

    @CommandBody
    val tp = tpSubCommand

    @CommandBody
    val move = moveSubCommand

    @CommandBody
    val look = lookSubCommand

    @CommandBody
    val edit = editSubCommand

    @CommandBody
    val undo = undoSubCommand

    @CommandBody
    val list = listSubCommand

    @CommandBody
    val clone = cloneSubCommand

    @CommandBody
    val reload = subCommand {
        execute<CommandSender> { sender, _, _ ->
            AdyeshachSettings.conf.reload()
            ResettableLazy.reset()
            sender.sendMessage("§c[Adyeshach] §7Reloaded.")
        }
    }
}