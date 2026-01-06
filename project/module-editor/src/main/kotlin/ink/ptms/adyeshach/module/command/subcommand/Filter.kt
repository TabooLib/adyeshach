package ink.ptms.adyeshach.module.command.subcommand

import ink.ptms.adyeshach.core.ADYESHACH_PREFIX
import ink.ptms.adyeshach.module.command.filterDerived
import ink.ptms.adyeshach.module.command.filterUneditable
import org.bukkit.command.CommandSender
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggestUncheck

/**
 * npc filter [derived|uneditable]
 *
 * 切换实体列表过滤开关
 */
val filterSubCommand = subCommand {
    dynamic("type") {
        suggestUncheck { listOf("derived", "uneditable") }
        execute<CommandSender> { sender, _, arg ->
            when (arg.lowercase()) {
                "derived" -> {
                    filterDerived = !filterDerived
                    val status = if (filterDerived) "enabled" else "disabled"
                    sender.sendMessage("${ADYESHACH_PREFIX}Filter derived entities: $status")
                }
                "uneditable" -> {
                    filterUneditable = !filterUneditable
                    val status = if (filterUneditable) "enabled" else "disabled"
                    sender.sendMessage("${ADYESHACH_PREFIX}Filter uneditable entities: $status")
                }
                else -> {
                    sender.sendMessage("${ADYESHACH_PREFIX}Unknown filter type: $arg")
                }
            }
        }
    }
    // 无参数时显示当前状态
    execute<CommandSender> { sender, _, _ ->
        sender.sendMessage("${ADYESHACH_PREFIX}Filter status:")
        sender.sendMessage("  derived: ${if (filterDerived) "enabled" else "disabled"}")
        sender.sendMessage("  uneditable: ${if (filterUneditable) "enabled" else "disabled"}")
    }
}
