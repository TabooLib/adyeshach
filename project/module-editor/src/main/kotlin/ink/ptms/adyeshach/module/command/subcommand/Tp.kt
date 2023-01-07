@file:Suppress("DuplicatedCode")

package ink.ptms.adyeshach.module.command.subcommand

import ink.ptms.adyeshach.core.util.sendLang
import ink.ptms.adyeshach.module.command.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.location
import taboolib.common.platform.command.subCommand
import taboolib.platform.util.toBukkitLocation
import taboolib.platform.util.toProxyLocation

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
            multiControl<EntitySource.Empty>(sender, ctx.self(), STANDARD_TP_TRACKER, unified = false) {
                sender.teleport(it.getLocation())
                if (!sender.isIgnoreNotice()) {
                    sender.sendLang("command-teleport-to-entity", it.id)
                }
            }
        }
        // 传送到当前位置
        literal("here") {
            execute<Player> { sender, ctx, _ ->
                multiControl<EntitySource.Empty>(sender, ctx["id"], STANDARD_TP_TRACKER, unified = false) {
                    it.teleport(sender.location)
                    if (!sender.isIgnoreNotice()) {
                        sender.sendLang("command-teleport-to-here", it.id)
                    }
                }
            }
        }
        // 传送到指定位置
        literal("to").location {
            execute<CommandSender> { sender, ctx, _ ->
                multiControl<EntitySource.Empty>(sender, ctx["id"], STANDARD_TP_TRACKER, unified = false) {
                    val loc = ctx.location(origin = it.getLocation().toProxyLocation())
                    if (loc.world == null) {
                        sender.sendLang("command-world-not-found", ctx["world"])
                        return@multiControl
                    }
                    it.teleport(loc.toBukkitLocation())
                    if (!sender.isIgnoreNotice()) {
                        sender.sendLang(
                            "command-teleport-to-location",
                            it.id,
                            loc.world!!,
                            format(loc.x),
                            format(loc.y),
                            format(loc.z),
                            format(loc.yaw),
                            format(loc.pitch)
                        )
                    }
                }
            }
        }
    }
    // 就近传送
    execute<Player> { sender, _, _ ->
        multiControl<RemoveEntitySource>(sender, STANDARD_TP_TRACKER) {
            sender.teleport(it.getLocation())
            sender.sendLang("command-teleport-to-entity", it.id)
        }
    }
}