package ink.ptms.adyeshach.module.command.subcommand

import ink.ptms.adyeshach.core.util.sendLang
import ink.ptms.adyeshach.module.command.EntitySource
import ink.ptms.adyeshach.module.command.EntityTracker
import ink.ptms.adyeshach.module.command.multiControl
import ink.ptms.adyeshach.module.command.suggestEntityList
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand

private const val STANDARD_CLONE_TRACKER = "clone"

/**
 * npc clone (id) (new-id)
 *
 * npc clone 1 2
 */
val cloneSubCommand = subCommand {
    dynamic("id") {
        suggestEntityList()
        dynamic("new-id") {
            // 定向克隆
            execute<Player> { sender, ctx, _ ->
                multiControl<EntitySource.Empty>(sender, ctx["id"], STANDARD_CLONE_TRACKER, unified = false) {
                    // 克隆
                    it.clone(ctx.self(), sender.location)
                    // 打印追踪器
                    EntityTracker.check(sender, STANDARD_CLONE_TRACKER, it)
                    // 提示信息
                    sender.sendLang("command-clone-success", it.id, ctx.self())
                }
            }
        }
        // 定向克隆（自动命名）
        execute<Player> { sender, ctx, _ ->
            multiControl<EntitySource.Empty>(sender, ctx.self(), STANDARD_CLONE_TRACKER, unified = false) {
                val manager = it.manager
                val newId = if (manager == null) {
                    // 没有管理器则固定后缀为 [id]_copy
                    if (it.id.endsWith("_copy")) it.id else "${it.id}_copy"
                } else {
                    // 有任何管理器则自动命名为 [id]_[序号]，例如 test 克隆后为 test_2
                    var i = 2
                    while (true) {
                        if (manager.getEntityById("${it.id}_$i").isEmpty()) {
                            break
                        }
                        i++
                    }
                    "${it.id}_$i"
                }
                // 克隆
                it.clone(newId, sender.location)
                // 打印追踪器
                EntityTracker.check(sender, STANDARD_CLONE_TRACKER, it)
                // 提示信息
                sender.sendLang("command-clone-success", it.id, newId)
            }
        }
    }
}