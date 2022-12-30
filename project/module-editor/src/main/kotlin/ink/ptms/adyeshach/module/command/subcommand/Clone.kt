package ink.ptms.adyeshach.module.command.subcommand

import ink.ptms.adyeshach.core.util.sendLang
import ink.ptms.adyeshach.module.command.EntitySource
import ink.ptms.adyeshach.module.command.EntityTracker
import ink.ptms.adyeshach.module.command.multiControl
import ink.ptms.adyeshach.module.command.suggestEntityList
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand

const val STANDARD_CLONE_TRACKER = "clone"

/**
 * npc clone (id) (new-id)
 *
 * npc clone 1 2
 */
val cloneSubCommand = subCommand {
    dynamic("id") {
        suggestEntityList()
        dynamic("new-id") {
            // 定向重命名
            execute<Player> { sender, ctx, _ ->
                multiControl<EntitySource.Empty>(sender, ctx.argument(-1), STANDARD_CLONE_TRACKER, unified = false) {
                    // 克隆
                    it.clone(ctx.argument(0), sender.location)
                    // 打印追踪器
                    EntityTracker.check(sender, STANDARD_CLONE_TRACKER, it)
                    // 提示信息
                    sender.sendLang("command-clone-success", it.id, ctx.argument(0))
                }
            }
        }
    }
}