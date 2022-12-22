package ink.ptms.adyeshach.module.command.subcommand

import ink.ptms.adyeshach.module.command.Command
import ink.ptms.adyeshach.module.command.mlc.MultiController
import ink.ptms.adyeshach.module.command.multiControl
import ink.ptms.adyeshach.module.command.suggestEntityList
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggestUncheck
import taboolib.platform.util.sendLang

val removeSubCommand = subCommand {
    dynamic("id") {
        suggestEntityList()
        dynamic("action") {
            suggestUncheck { listOf("a", "all") }
            execute<CommandSender> { sender, ctx, _ ->
                val npcList = Command.finder.getEntitiesFromIdOrUniqueId(ctx.argument(-1), if (sender is Player) sender else null)
                if (npcList.isEmpty()) {
                    sender.sendLang("command-find-empty")
                    return@execute
                }
                // 删除单位
                npcList.forEach { it.remove() }
                // 打印列表
                MultiController.check(sender)
                // 提示信息
                when (ctx.argument(0)) {
                    // 删除全部
                    "a", "all" -> sender.sendLang("command-remove-success-all", ctx.argument(-1), npcList.first().uniqueId)
                    // 删除单个
                    "c" -> sender.sendLang("command-remove-success", ctx.argument(-1), npcList.first().uniqueId)
                }
            }
        }
        // 定向删除
        execute<CommandSender> { sender, ctx, _ ->
            multiControl(sender, ctx.argument(0), "remove") {
                it.remove()
                sender.sendLang("command-remove-success", it.id, it.uniqueId)
            }
        }
    }
    // 就近删除
    execute<Player> { sender, _, _ -> multiControl(sender, "remove") }
}