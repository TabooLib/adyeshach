package ink.ptms.adyeshach.module.command.subcommand

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.module.command.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggestUncheck
import taboolib.platform.util.sendLang

const val STANDARD_REMOVE_TRACKER = "remove"

/**
 * npc remove (id)? (action)?
 *
 * npc remove 1
 * npc remove 1 a
 */
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
                // 打印追踪器
                EntityTracker.check(sender, STANDARD_REMOVE_TRACKER, npcList.first())
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
            multiControl<RemoveEntitySource>(sender, ctx.argument(0), STANDARD_REMOVE_TRACKER) {
                it.remove()
                sender.sendLang("command-remove-success", it.id, it.uniqueId)
            }
        }
    }
    // 就近删除
    execute<Player> { sender, _, _ -> multiControl<RemoveEntitySource>(sender, STANDARD_REMOVE_TRACKER) }
}

class RemoveEntitySource(elements: List<EntityInstance>) : EntitySource(elements) {

    override fun isUpdated(entity: EntityInstance): Boolean {
        return entity.isRemoved
    }

    override fun extraArgs(entity: EntityInstance): Array<Any> {
        return emptyArray()
    }
}