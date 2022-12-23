@file:Suppress("DuplicatedCode")

package ink.ptms.adyeshach.module.command.subcommand

import ink.ptms.adyeshach.module.command.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggestUncheck
import taboolib.common5.cdouble
import taboolib.common5.cfloat
import taboolib.platform.util.sendLang

const val STANDARD_TP_TRACKER = "teleport"

/**
 * npc tp (id)? (method) (...)?
 *
 * npc tp 1
 * npc tp 1 here
 * npc tp 1 to world 0 0 0 —— 传送到指定位置
 * npc tp 1 to ~ ~ ~1 ~    —— 向上传送 1 格
 */
val tpSubCommand = subCommand {
    dynamic("id") {
        suggestEntityList()
        // 定向传送
        execute<Player> { sender, ctx, _ ->
            multiControl<EntitySource.Empty>(sender, ctx.argument(0), STANDARD_TP_TRACKER) {
                sender.teleport(it.getLocation())
                sender.sendLang("command-teleport-to-entity", ctx.argument(0))
            }
        }
        // 传送到当前位置
        literal("here") {
            execute<Player> { sender, ctx, _ ->
                multiControl<EntitySource.Empty>(sender, ctx.argument(-1), STANDARD_TP_TRACKER) {
                    it.teleport(sender.location)
                    sender.sendLang("command-teleport-to-here", ctx.argument(-1))
                }
            }
        }
        // 传送到指定位置
        literal("to") {
            dynamic("world") {
                suggestUncheck { worlds() }
                dynamic("x") {
                    suggestion<Player>(uncheck = true) { sender, _ -> listOf(format(sender.location.x), "~") }
                    dynamic("y") {
                        suggestion<Player>(uncheck = true) { sender, _ -> listOf(format(sender.location.y), "~") }
                        dynamic("z") {
                            suggestion<Player>(uncheck = true) { sender, _ -> listOf(format(sender.location.z), "~") }
                            execute<CommandSender> { sender, ctx, _ ->
                                val id = ctx.argument(-5)
                                val world = ctx.argument(-3)
                                val x = ctx.argument(-2)
                                val y = ctx.argument(-1)
                                val z = ctx.argument(0)
                                teleport(sender, id, world, x, y, z, "~", "~")
                            }
                            dynamic("yaw") {
                                suggestion<Player>(uncheck = true) { sender, _ -> listOf(format(sender.location.y), "~") }
                                dynamic("pitch") {
                                    suggestion<Player>(uncheck = true) { sender, _ -> listOf(format(sender.location.z), "~") }
                                    execute<CommandSender> { sender, ctx, _ ->
                                        val id = ctx.argument(-7)
                                        val world = ctx.argument(-5)
                                        val x = ctx.argument(-4)
                                        val y = ctx.argument(-3)
                                        val z = ctx.argument(-2)
                                        val yaw = ctx.argument(-1)
                                        val pitch = ctx.argument(0)
                                        teleport(sender, id, world, x, y, z, yaw, pitch)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    // 就近传送
    execute<Player> { sender, _, _ -> multiControl<RemoveEntitySource>(sender, STANDARD_TP_TRACKER) }
}

private fun teleport(sender: CommandSender, id: String, world: String, x: String, y: String, z: String, yaw: String, pitch: String) {
    multiControl<EntitySource.Empty>(sender, id, STANDARD_TP_TRACKER) {
        val loc = Location(
            if (world == "~") it.world else Bukkit.getWorld(world),
            if (x.startsWith("~")) it.x + x.substring(1).cdouble else x.cdouble,
            if (y.startsWith("~")) it.y + y.substring(1).cdouble else y.cdouble,
            if (z.startsWith("~")) it.z + z.substring(1).cdouble else z.cdouble,
            if (yaw.startsWith("~")) it.yaw + yaw.substring(1).cfloat else yaw.cfloat,
            if (pitch.startsWith("~")) it.pitch + pitch.substring(1).cfloat else pitch.cfloat
        )
        if (loc.world == null) {
            sender.sendLang("command-world-not-found", world)
            return@multiControl
        }
        it.teleport(loc)
        sender.sendLang("command-teleport-to-location", id, loc.world.name, format(loc.x), format(loc.y), format(loc.z), format(loc.yaw), format(loc.pitch))
    }
}