package ink.ptms.adyeshach.module.command.subcommand

import ink.ptms.adyeshach.module.command.Command
import ink.ptms.adyeshach.module.command.multiControl
import ink.ptms.adyeshach.module.command.suggestEntityList
import ink.ptms.adyeshach.module.editor.EditPanel
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggestUncheck
import taboolib.platform.util.sendLang

val editSubCommand = subCommand {
    dynamic("id") {
        suggestEntityList()
        dynamic("action") {
            suggestUncheck { listOf("a", "all") }
            execute<Player> { sender, ctx, _ ->
                val npcList = Command.finder.getEntitiesFromIdOrUniqueId(ctx.argument(-1), sender)
                if (npcList.isEmpty()) {
                    sender.sendLang("command-find-empty")
                    return@execute
                }
            }
        }
        // 定向编辑
        execute<Player> { sender, ctx, _ -> multiControl(sender, ctx.argument(0), "edit", all = false) { EditPanel(sender, it).open() } }
    }
    // 就近编辑
    execute<Player> { sender, _, _ -> multiControl(sender, "edit") }
}