package ink.ptms.adyeshach.module.command.subcommand

import ink.ptms.adyeshach.module.command.Command
import ink.ptms.adyeshach.module.command.multiControl
import ink.ptms.adyeshach.module.command.suggestEntityList
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggestUncheck
import taboolib.platform.util.sendLang

val renameSubCommand = subCommand {
    dynamic("id") {
        suggestEntityList()
        dynamic("new-id") {
            // 特殊行为
            dynamic("action") {
                suggestUncheck { listOf("a", "all") }
                execute<CommandSender> { sender, ctx, _ ->
                    val npcList = Command.finder.getEntitiesFromIdOrUniqueId(ctx.argument(-2), if (sender is Player) sender else null)
                    if (npcList.isEmpty()) {
                        sender.sendLang("command-find-empty")
                        return@execute
                    }
                    val old = npcList.first().id
                    // 更名
                    npcList.forEach { it.id = ctx.argument(-1) }
                    // 提示信息
                    sender.sendLang("command-rename-success-all", old, ctx.argument(-1))
                }
            }
            // 定向重命名
            execute<CommandSender> { sender, ctx, _ ->
                multiControl(sender, ctx.argument(-1), "rename") {
                    val old = it.id
                    // 更名
                    it.id = ctx.argument(0)
                    // 提示信息
                    sender.sendLang("command-rename-success", old, it.id)
                }
            }
        }
    }
}