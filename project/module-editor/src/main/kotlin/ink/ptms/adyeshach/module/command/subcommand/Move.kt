@file:Suppress("DuplicatedCode")

package ink.ptms.adyeshach.module.command.subcommand

import ink.ptms.adyeshach.core.util.sendLang
import ink.ptms.adyeshach.module.command.*
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.*
import taboolib.platform.util.toProxyLocation

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
                multiControl<EntitySource.Empty>(sender, ctx["id"], STANDARD_MOVE_TRACKER, unified = false) {
                    if (it.world != sender.world) {
                        sender.sendLang("command-world-different", ctx["id"])
                        return@multiControl
                    }
                    if (it.hasVehicle()) {
                        sender.sendLang("command-move-has-vehicle", ctx["id"], it.getVehicle()!!.id)
                        return@multiControl
                    }
                    it.moveTarget = sender.location
                    if (!sender.isIgnoreNotice()) {
                        sender.sendLang("command-move-to-here", it.id)
                    }
                }
            }
        }
        // 移动到指定位置
        literal("to").xyz {
            execute<CommandSender> { sender, ctx, _ ->
                multiControl<EntitySource.Empty>(sender, ctx["id"], STANDARD_MOVE_TRACKER, unified = false) {
                    if (it.hasVehicle()) {
                        sender.sendLang("command-move-has-vehicle", ctx["id"], it.getVehicle()!!.id)
                        return@multiControl
                    }
                    val origin = it.getLocation().toProxyLocation()
                    val loc = Location(it.world, ctx.x("x", origin), ctx.y("y", origin), ctx.z("z", origin))
                    it.moveTarget = loc
                    if (!sender.isIgnoreNotice()) {
                        sender.sendLang("command-move-to-location", it.id, format(loc.x), format(loc.y), format(loc.z))
                    }
                }
            }
        }
    }
}