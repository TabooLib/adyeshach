package ink.ptms.adyeshach.module.command.subcommand

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.module.command.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggestUncheck
import taboolib.platform.util.sendLang

const val STANDARD_RENAME_TRACKER = "rename"

/**
 * npc rename (id) (new-id) (action)?
 *
 * npc rename 1 2
 * npc rename 1 2 a
 */
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
                    // 打印追踪器
                    EntityTracker.check(sender, STANDARD_RENAME_TRACKER, npcList.first())
                    // 提示信息
                    sender.sendLang("command-rename-success-all", old, ctx.argument(-1))
                }
            }
            // 定向重命名
            execute<CommandSender> { sender, ctx, _ ->
                multiControl<RenameEntitySource>(sender, ctx.argument(-1), STANDARD_RENAME_TRACKER) {
                    val old = it.id
                    // 更名
                    it.id = ctx.argument(0)
                    // 打印追踪器
                    EntityTracker.check(sender, STANDARD_RENAME_TRACKER, it)
                    // 提示信息
                    sender.sendLang("command-rename-success", old, it.id)
                }
            }
        }
    }
}

class RenameEntitySource(elements: List<EntityInstance>) : EntitySource(elements) {

    val idMap = elements.associate { it.uniqueId to it.id }

    override fun isUpdated(entity: EntityInstance): Boolean {
        return idMap[entity.uniqueId] != entity.id
    }

    override fun extraArgs(entity: EntityInstance): Array<Any> {
        return arrayOf(idMap[entity.uniqueId] to "old_id", entity.id to "new_id")
    }
}