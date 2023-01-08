package ink.ptms.adyeshach.module.command.subcommand

import ink.ptms.adyeshach.core.util.sendLang
import ink.ptms.adyeshach.module.command.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand

private const val STANDARD_PASSENGER_ADD_TRACKER = "passenger-add"

private const val STANDARD_PASSENGER_REMOVE_TRACKER = "passenger-remove"

private const val STANDARD_PASSENGER_RESET_TRACKER = "passenger-reset"

/**
 * npc passenger (id) add (other-id)
 * npc passenger (id) remove (other-id)
 * npc passenger (id) reset
 */
val passengerSubCommand = subCommand {
    dynamic("id") {
        suggestEntityList()
        literal("add") {
            dynamic("other-id") {
                suggestEntityList()
                execute<CommandSender> { sender, ctx, _ ->
                    val other = Command.finder.getEntitiesFromIdOrUniqueId(ctx["other-id"], sender as? Player).filter { !it.isDerived() }
                    if (other.isEmpty()) {
                        sender.sendLang("command-passenger-not-found", ctx["other-id"])
                        return@execute
                    }
                    multiControl<EntitySource.Empty>(sender, ctx["id"], STANDARD_PASSENGER_ADD_TRACKER, unified = false) {
                        // 添加乘客
                        it.addPassenger(*other.toTypedArray())
                        // 打印追踪器
                        EntityTracker.check(sender, STANDARD_PASSENGER_ADD_TRACKER, it)
                        // 提示信息
                        sender.sendLang("command-passenger-add-success", ctx["other-id"], it.id)
                    }
                }
            }
        }
        literal("remove") {
            dynamic("other-id") {
                suggestEntityList()
                execute<CommandSender> { sender, ctx, _ ->
                    multiControl<EntitySource.Empty>(sender, ctx["id"], STANDARD_PASSENGER_REMOVE_TRACKER, unified = false) {
                        // 移除乘客
                        it.removePassenger(ctx["other-id"])
                        // 打印追踪器
                        EntityTracker.check(sender, STANDARD_PASSENGER_REMOVE_TRACKER, it)
                        // 提示信息
                        sender.sendLang("command-passenger-remove-success", ctx["other-id"], it.id)
                    }
                }
            }
        }
        literal("reset") {
            execute<CommandSender> { sender, ctx, _ ->
                multiControl<EntitySource.Empty>(sender, ctx["id"], STANDARD_PASSENGER_RESET_TRACKER, unified = false) {
                    // 移除乘客
                    it.clearPassengers()
                    // 打印追踪器
                    EntityTracker.check(sender, STANDARD_PASSENGER_RESET_TRACKER, it)
                    // 提示信息
                    sender.sendLang("command-passenger-reset-success", it.id)
                }
            }
        }
    }
}