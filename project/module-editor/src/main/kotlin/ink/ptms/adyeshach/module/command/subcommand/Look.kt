@file:Suppress("DuplicatedCode")

package ink.ptms.adyeshach.module.command.subcommand

import ink.ptms.adyeshach.module.command.*
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggestUncheck
import taboolib.common5.cdouble
import taboolib.common5.cfloat
import ink.ptms.adyeshach.core.util.sendLang

const val STANDARD_LOOK_TRACKER = "look"

/**
 * npc look (id)? (method) (...)?
 *
 * npc look 1 here
 * npc look 1 to world 0 0 0 —— 移动到指定位置
 */
val lookSubCommand = subCommand {
    dynamic("id") {
        suggestEntityList()
        // 看向当前位置
        literal("here") {
            execute<Player> { sender, ctx, _ ->
                multiControl<EntitySource.Empty>(sender, ctx.argument(-1), STANDARD_LOOK_TRACKER) {
                    if (it.world != sender.world) {
                        sender.sendLang("command-world-different", ctx.argument(-1))
                        return@multiControl
                    }
                    it.setHeadRotation(sender.eyeLocation, forceUpdate = true)
                    if (!sender.isIgnoreNotice()) {
                        sender.sendLang("command-look-to-here", it.id)
                    }
                }
            }
        }
        // 看向我的方向
        literal("like") {
            execute<Player> { sender, ctx, _ ->
                multiControl<EntitySource.Empty>(sender, ctx.argument(-1), STANDARD_LOOK_TRACKER) {
                    if (it.world != sender.world) {
                        sender.sendLang("command-world-different", ctx.argument(-1))
                        return@multiControl
                    }
                    it.setHeadRotation(sender.location.yaw, sender.location.pitch, forceUpdate = true)
                    if (!sender.isIgnoreNotice()) {
                        sender.sendLang("command-look-with", it.id, format(sender.location.yaw), format(sender.location.pitch))
                    }
                }
            }
        }
        // 看向指定方向
        literal("with") {
            dynamic("yaw") {
                suggestUncheck { listOf("~") }
                dynamic("pitch") {
                    suggestUncheck { listOf("~") }
                    execute<CommandSender> { sender, ctx, _ ->
                        val id = ctx.argument(-3)
                        val yaw = ctx.argument(-1)
                        val pitch = ctx.argument(0)
                        multiControl<EntitySource.Empty>(sender, id, STANDARD_LOOK_TRACKER) {
                            val y = if (yaw.startsWith("~")) it.yaw + yaw.substring(1).cfloat else yaw.cfloat
                            val p = if (pitch.startsWith("~")) it.pitch + pitch.substring(1).cfloat else pitch.cfloat
                            it.setHeadRotation(y, p, forceUpdate = true)
                            if (!sender.isIgnoreNotice()) {
                                sender.sendLang("command-look-with", it.id, format(y), format(p))
                            }
                        }
                    }
                }
            }
        }
        // 看向指定位置
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
                            multiControl<EntitySource.Empty>(sender, id, STANDARD_LOOK_TRACKER) {
                                val loc = Location(
                                    it.world,
                                    if (x.startsWith("~")) it.x + x.substring(1).cdouble else x.cdouble,
                                    if (y.startsWith("~")) it.y + y.substring(1).cdouble else y.cdouble,
                                    if (z.startsWith("~")) it.z + z.substring(1).cdouble else z.cdouble,
                                )
                                it.setHeadRotation(loc, forceUpdate = true)
                                if (!sender.isIgnoreNotice()) {
                                    sender.sendLang("command-look-to-location", it.id, format(loc.x), format(loc.y), format(loc.z))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}