@file:Suppress("DuplicatedCode")

package ink.ptms.adyeshach.module.command.subcommand

import ink.ptms.adyeshach.module.command.*
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.common5.cdouble
import taboolib.platform.util.sendLang

const val STANDARD_MOVE_TRACKER = "move"

/**
 * npc move (id)? (method) (...)?
 *
 * npc move 1 here
 * npc move 1 to 0 0 0 —— 移动到指定位置
 * npc move 1 to ~1 ~ ~    —— 向 x 方向移动 1 格
 */
val moveSubCommand = subCommand {
    dynamic("id") {
        suggestEntityList()
        // 移动到当前位置
        literal("here") {
            execute<Player> { sender, ctx, _ ->
                multiControl<EntitySource.Empty>(sender, ctx.argument(-1), STANDARD_MOVE_TRACKER) {
                    if (it.world != sender.world) {
                        sender.sendLang("command-world-different", ctx.argument(-1))
                        return@multiControl
                    }
                    if (it.hasVehicle()) {
                        sender.sendLang("command-move-has-vehicle", ctx.argument(-1), it.getVehicle()!!.id)
                        return@multiControl
                    }
                    it.moveTarget = sender.location
                    sender.sendLang("command-move-to-here", it.id)
                }
            }
        }
        // 移动到指定位置
        literal("to") {
            dynamic("x") {
                suggestion<Player>(uncheck = true) { sender, _ -> listOf(format(sender.location.x), "~") }
                dynamic("y") {
                    suggestion<Player>(uncheck = true) { sender, _ -> listOf(format(sender.location.y), "~") }
                    dynamic("z") {
                        suggestion<Player>(uncheck = true) { sender, _ -> listOf(format(sender.location.z), "~") }
                        execute<CommandSender> { sender, ctx, _ ->
                            val id = ctx.argument(-4)
                            val x = ctx.argument(-2)
                            val y = ctx.argument(-1)
                            val z = ctx.argument(0)
                            multiControl<EntitySource.Empty>(sender, id, STANDARD_MOVE_TRACKER) {
                                if (it.hasVehicle()) {
                                    sender.sendLang("command-move-has-vehicle", id, it.getVehicle()!!.id)
                                    return@multiControl
                                }
                                val loc = Location(
                                    it.world,
                                    if (x.startsWith("~")) it.x + x.substring(1).cdouble else x.cdouble,
                                    if (y.startsWith("~")) it.y + y.substring(1).cdouble else y.cdouble,
                                    if (z.startsWith("~")) it.z + z.substring(1).cdouble else z.cdouble,
                                )
                                it.moveTarget = loc
                                sender.sendLang("command-move-to-location", it.id, format(loc.x), format(loc.y), format(loc.z))
                            }
                        }
                    }
                }
            }
        }
    }
}