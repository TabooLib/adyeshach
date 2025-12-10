package ink.ptms.adyeshach.module.command.subcommand

import ink.ptms.adyeshach.core.util.sendLang
import ink.ptms.adyeshach.module.command.*
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.command.subcommand.Viewer
 *
 * 可见性调试指令
 *
 * npc viewer (id) info                    - 查看可见性信息
 * npc viewer (id) show [player]           - 对玩家显示实体
 * npc viewer (id) hide [player]           - 对玩家隐藏实体
 * npc viewer (id) add [player]            - 添加观察者
 * npc viewer (id) remove [player]         - 移除观察者
 * npc viewer (id) clear                   - 清空观察者
 * npc viewer (id) respawn [player]        - 对玩家重新生成实体
 *
 * @author 坏黑
 * @since 2024/12/11
 */
val viewerSubCommand = subCommand {
    dynamic("id") {
        suggestEntityList()
        // 查看可见性信息
        literal("info") {
            execute<CommandSender> { sender, ctx, _ ->
                multiControl<EntitySource.Empty>(sender, ctx["id"], "viewer-info", unified = false) { entity ->
                    sender.sendMessage("§5§l‹ ›§f §7可见性信息: §f${entity.id}")
                    sender.sendMessage("§5§l‹ ›§f §7  - UniqueId: §f${entity.uniqueId}")
                    sender.sendMessage("§5§l‹ ›§f §7  - Index: §f${entity.index}")
                    sender.sendMessage("§5§l‹ ›§f §7  - 是否伴生实体: §f${entity.isCompanion()}")
                    
                    // 宿主信息
                    val host = entity.getHost()
                    if (host != null) {
                        sender.sendMessage("§5§l‹ ›§f §7  - 宿主: §f${host.id} §7(${host.uniqueId})")
                    }
                    
                    // 可视距离
                    sender.sendMessage("§5§l‹ ›§f §7  - 可视距离: §f${entity.visibleDistance}")
                    sender.sendMessage("§5§l‹ ›§f §7  - 加载后自动显示: §f${entity.visibleAfterLoaded}")
                    
                    // 观察者列表
                    val viewers = entity.viewPlayers.viewers
                    sender.sendMessage("§5§l‹ ›§f §7  - 观察者列表 (${viewers.size}):")
                    if (viewers.isEmpty()) {
                        sender.sendMessage("§5§l‹ ›§f §7    §c(空)")
                    } else {
                        viewers.take(10).forEach { name ->
                            val inVisible = name in entity.viewPlayers.visible
                            val status = if (inVisible) "§a可见" else "§c不可见"
                            sender.sendMessage("§5§l‹ ›§f §7    - §f$name §7[$status§7]")
                        }
                        if (viewers.size > 10) {
                            sender.sendMessage("§5§l‹ ›§f §7    §8... 还有 ${viewers.size - 10} 个")
                        }
                    }
                    
                    // 可见玩家列表
                    val visible = entity.viewPlayers.visible
                    sender.sendMessage("§5§l‹ ›§f §7  - 当前可见玩家 (${visible.size}):")
                    if (visible.isEmpty()) {
                        sender.sendMessage("§5§l‹ ›§f §7    §c(空)")
                    } else {
                        visible.take(10).forEach { name ->
                            sender.sendMessage("§5§l‹ ›§f §7    - §f$name")
                        }
                        if (visible.size > 10) {
                            sender.sendMessage("§5§l‹ ›§f §7    §8... 还有 ${visible.size - 10} 个")
                        }
                    }
                    
                    // 伴生实体信息
                    val companions = entity.getCompanions()
                    if (companions.isNotEmpty()) {
                        sender.sendMessage("§5§l‹ ›§f §7  - 伴生实体可见状态:")
                        companions.forEach { companion ->
                            val cVisible = companion.viewPlayers.visible.size
                            val cViewers = companion.viewPlayers.viewers.size
                            sender.sendMessage("§5§l‹ ›§f §7    - §f${companion.id}§7: viewers=$cViewers, visible=$cVisible")
                        }
                    }
                }
            }
        }
        // 对玩家显示实体
        literal("show") {
            execute<Player> { sender, ctx, _ ->
                multiControl<EntitySource.Empty>(sender, ctx["id"], "viewer-show", unified = false) { entity ->
                    if (entity.isCompanion()) {
                        sender.sendLang("command-viewer-is-companion", entity.id)
                        return@multiControl
                    }
                    val result = entity.visible(sender, true)
                    sender.sendLang("command-viewer-show-result", entity.id, if (result) "§a成功" else "§c失败")
                }
            }
            dynamic("player") {
                suggestion<CommandSender>(uncheck = true) { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }
                execute<CommandSender> { sender, ctx, _ ->
                    val target = Bukkit.getPlayerExact(ctx["player"])
                    if (target == null) {
                        sender.sendLang("command-viewer-player-not-found", ctx["player"])
                        return@execute
                    }
                    multiControl<EntitySource.Empty>(sender, ctx["id"], "viewer-show", unified = false) { entity ->
                        if (entity.isCompanion()) {
                            sender.sendLang("command-viewer-is-companion", entity.id)
                            return@multiControl
                        }
                        val result = entity.visible(target, true)
                        sender.sendLang("command-viewer-show-result-player", entity.id, target.name, if (result) "§a成功" else "§c失败")
                    }
                }
            }
        }
        // 对玩家隐藏实体
        literal("hide") {
            execute<Player> { sender, ctx, _ ->
                multiControl<EntitySource.Empty>(sender, ctx["id"], "viewer-hide", unified = false) { entity ->
                    if (entity.isCompanion()) {
                        sender.sendLang("command-viewer-is-companion", entity.id)
                        return@multiControl
                    }
                    val result = entity.visible(sender, false)
                    sender.sendLang("command-viewer-hide-result", entity.id, if (result) "§a成功" else "§c失败")
                }
            }
            dynamic("player") {
                suggestion<CommandSender>(uncheck = true) { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }
                execute<CommandSender> { sender, ctx, _ ->
                    val target = Bukkit.getPlayerExact(ctx["player"])
                    if (target == null) {
                        sender.sendLang("command-viewer-player-not-found", ctx["player"])
                        return@execute
                    }
                    multiControl<EntitySource.Empty>(sender, ctx["id"], "viewer-hide", unified = false) { entity ->
                        if (entity.isCompanion()) {
                            sender.sendLang("command-viewer-is-companion", entity.id)
                            return@multiControl
                        }
                        val result = entity.visible(target, false)
                        sender.sendLang("command-viewer-hide-result-player", entity.id, target.name, if (result) "§a成功" else "§c失败")
                    }
                }
            }
        }
        // 添加观察者
        literal("add") {
            execute<Player> { sender, ctx, _ ->
                multiControl<EntitySource.Empty>(sender, ctx["id"], "viewer-add", unified = false) { entity ->
                    if (entity.isCompanion()) {
                        sender.sendLang("command-viewer-is-companion", entity.id)
                        return@multiControl
                    }
                    entity.addViewer(sender)
                    sender.sendLang("command-viewer-add-result", entity.id)
                }
            }
            dynamic("player") {
                suggestion<CommandSender>(uncheck = true) { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }
                execute<CommandSender> { sender, ctx, _ ->
                    val target = Bukkit.getPlayerExact(ctx["player"])
                    if (target == null) {
                        sender.sendLang("command-viewer-player-not-found", ctx["player"])
                        return@execute
                    }
                    multiControl<EntitySource.Empty>(sender, ctx["id"], "viewer-add", unified = false) { entity ->
                        if (entity.isCompanion()) {
                            sender.sendLang("command-viewer-is-companion", entity.id)
                            return@multiControl
                        }
                        entity.addViewer(target)
                        sender.sendLang("command-viewer-add-result-player", entity.id, target.name)
                    }
                }
            }
        }
        // 移除观察者
        literal("remove") {
            execute<Player> { sender, ctx, _ ->
                multiControl<EntitySource.Empty>(sender, ctx["id"], "viewer-remove", unified = false) { entity ->
                    if (entity.isCompanion()) {
                        sender.sendLang("command-viewer-is-companion", entity.id)
                        return@multiControl
                    }
                    entity.removeViewer(sender)
                    sender.sendLang("command-viewer-remove-result", entity.id)
                }
            }
            dynamic("player") {
                suggestion<CommandSender>(uncheck = true) { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }
                execute<CommandSender> { sender, ctx, _ ->
                    val target = Bukkit.getPlayerExact(ctx["player"])
                    if (target == null) {
                        sender.sendLang("command-viewer-player-not-found", ctx["player"])
                        return@execute
                    }
                    multiControl<EntitySource.Empty>(sender, ctx["id"], "viewer-remove", unified = false) { entity ->
                        if (entity.isCompanion()) {
                            sender.sendLang("command-viewer-is-companion", entity.id)
                            return@multiControl
                        }
                        entity.removeViewer(target)
                        sender.sendLang("command-viewer-remove-result-player", entity.id, target.name)
                    }
                }
            }
        }
        // 清空观察者
        literal("clear") {
            execute<CommandSender> { sender, ctx, _ ->
                multiControl<EntitySource.Empty>(sender, ctx["id"], "viewer-clear", unified = false) { entity ->
                    if (entity.isCompanion()) {
                        sender.sendLang("command-viewer-is-companion", entity.id)
                        return@multiControl
                    }
                    val count = entity.viewPlayers.viewers.size
                    entity.clearViewer()
                    sender.sendLang("command-viewer-clear-result", entity.id, count.toString())
                }
            }
        }
        // 重新生成实体
        literal("respawn") {
            execute<Player> { sender, ctx, _ ->
                multiControl<EntitySource.Empty>(sender, ctx["id"], "viewer-respawn", unified = false) { entity ->
                    entity.visible(sender, false)
                    entity.visible(sender, true)
                    sender.sendLang("command-viewer-respawn-result", entity.id)
                }
            }
            dynamic("player") {
                suggestion<CommandSender>(uncheck = true) { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }
                execute<CommandSender> { sender, ctx, _ ->
                    val target = Bukkit.getPlayerExact(ctx["player"])
                    if (target == null) {
                        sender.sendLang("command-viewer-player-not-found", ctx["player"])
                        return@execute
                    }
                    multiControl<EntitySource.Empty>(sender, ctx["id"], "viewer-respawn", unified = false) { entity ->
                        entity.visible(target, false)
                        entity.visible(target, true)
                        sender.sendLang("command-viewer-respawn-result-player", entity.id, target.name)
                    }
                }
            }
        }
    }
}
