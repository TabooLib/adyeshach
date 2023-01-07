@file:Suppress("DuplicatedCode")

package ink.ptms.adyeshach.module.command.subcommand

import ink.ptms.adyeshach.core.util.sendLang
import ink.ptms.adyeshach.module.command.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.*
import taboolib.platform.util.toBukkitLocation
import taboolib.platform.util.toProxyLocation

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
                multiControl<EntitySource.Empty>(sender, ctx["id"], STANDARD_LOOK_TRACKER, unified = false) {
                    if (it.world != sender.world) {
                        sender.sendLang("command-world-different", ctx["id"])
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
                multiControl<EntitySource.Empty>(sender, ctx["id"], STANDARD_LOOK_TRACKER, unified = false) {
                    if (it.world != sender.world) {
                        sender.sendLang("command-world-different", ctx["id"])
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
        literal("with").euler {
            execute<CommandSender> { sender, ctx, _ ->
                multiControl<EntitySource.Empty>(sender, ctx["id"], STANDARD_LOOK_TRACKER, unified = false) {
                    val origin = it.getLocation().toProxyLocation()
                    val y = ctx.yaw("yaw", origin)
                    val p = ctx.pitch("pitch", origin)
                    it.setHeadRotation(y, p, forceUpdate = true)
                    if (!sender.isIgnoreNotice()) {
                        sender.sendLang("command-look-with", it.id, format(y), format(p))
                    }
                }
            }
        }
        // 看向指定位置
        literal("to").xyz {
            execute<CommandSender> { sender, ctx, _ ->
                multiControl<EntitySource.Empty>(sender, ctx["id"], STANDARD_LOOK_TRACKER, unified = false) {
                    val loc = ctx.locationWithoutWorld(origin = it.getLocation().toProxyLocation())
                    it.setHeadRotation(loc.toBukkitLocation(), forceUpdate = true)
                    if (!sender.isIgnoreNotice()) {
                        sender.sendLang("command-look-to-location", it.id, format(loc.x), format(loc.y), format(loc.z))
                    }
                }
            }
        }
    }
}