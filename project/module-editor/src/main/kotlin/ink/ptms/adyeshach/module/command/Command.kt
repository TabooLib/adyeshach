package ink.ptms.adyeshach.module.command

import ink.ptms.adyeshach.core.ADYESHACH_PREFIX
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachSettings
import ink.ptms.adyeshach.impl.DefaultAdyeshachBooster
import ink.ptms.adyeshach.impl.entity.controller.KetherController
import ink.ptms.adyeshach.module.command.subcommand.*
import org.bukkit.command.CommandSender
import taboolib.common.platform.command.*
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
@Suppress("ObjectPropertyName")
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
    val passenger = passengerSubCommand

    @CommandBody(aliases = ["copy"])
    val clone = cloneSubCommand

    @CommandBody
    val edit = editSubCommand

    @CommandBody
    val undo = undoSubCommand

    @CommandBody(aliases = ["near"])
    val list = listSubCommand

    @CommandBody
    val `save-all` = subCommandExec<CommandSender> {
        DefaultAdyeshachBooster.api.localPublicEntityManager.onSave()
        sender.sendMessage("${ADYESHACH_PREFIX}Saved.")
    }

    @CommandBody
    val reload = subCommand {
        execute<CommandSender> { sender, _, _ ->
            AdyeshachSettings.conf.reload()
            KetherController.init()
            ResettableLazy.reset()
            sender.sendMessage("${ADYESHACH_PREFIX}Reloaded.")
        }
    }
}