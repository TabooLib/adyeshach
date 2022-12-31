package ink.ptms.adyeshach.module.command.subcommand

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.util.sendLang
import ink.ptms.adyeshach.module.command.EntitySource
import ink.ptms.adyeshach.module.command.EntityTracker
import ink.ptms.adyeshach.module.command.multiControl
import ink.ptms.adyeshach.module.command.suggestEntityList
import org.bukkit.command.CommandSender
import taboolib.common.platform.command.subCommand

const val STANDARD_RENAME_TRACKER = "rename"

/**
 * npc rename (id) (new-id)
 *
 * npc rename 1 2
 */
val renameSubCommand = subCommand {
    dynamic("id") {
        suggestEntityList()
        dynamic("new-id") {
            // 定向重命名
            execute<CommandSender> { sender, ctx, _ ->
                multiControl<RenameEntitySource>(sender, ctx.argument(-1), STANDARD_RENAME_TRACKER, unified = false) {
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

class RenameEntitySource(elements: MutableList<EntityInstance>) : EntitySource(elements) {

    val idMap = elements.associate { it.uniqueId to it.id }

    override fun isUpdated(entity: EntityInstance): Boolean {
        return idMap[entity.uniqueId] != entity.id
    }

    override fun extraArgs(entity: EntityInstance): Array<Any> {
        return arrayOf(idMap[entity.uniqueId] to "old_id", entity.id to "new_id")
    }
}