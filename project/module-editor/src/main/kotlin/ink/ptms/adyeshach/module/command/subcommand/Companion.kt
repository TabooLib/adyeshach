package ink.ptms.adyeshach.module.command.subcommand

import ink.ptms.adyeshach.core.util.sendLang
import ink.ptms.adyeshach.module.command.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.command.subcommand.Companion
 *
 * 伴生关系测试指令
 *
 * npc companion (id) add (other-id)     - 将 other-id 设为 id 的伴生实体
 * npc companion (id) remove (other-id)  - 移除 id 的伴生实体 other-id
 * npc companion (id) clear              - 清空 id 的所有伴生实体
 * npc companion (id) host (host-id)     - 将 id 归属给 host-id
 * npc companion (id) unhost             - 解除 id 的归属关系
 * npc companion (id) info               - 查看 id 的伴生关系信息
 *
 * @author 坏黑
 * @since 2024/12/11
 */
val companionSubCommand = subCommand {
    dynamic("id") {
        suggestEntityList()
        // 添加伴生实体
        literal("add") {
            dynamic("other-id") {
                suggestEntityList()
                execute<CommandSender> { sender, ctx, _ ->
                    val others = Command.finder.getEntitiesFromIdOrUniqueId(ctx["other-id"], sender as? Player).filter { !it.isDerived() }
                    if (others.isEmpty()) {
                        sender.sendLang("command-companion-not-found", ctx["other-id"])
                        return@execute
                    }
                    multiControl<EntitySource.Empty>(sender, ctx["id"], "companion-add", unified = false) { entity ->
                        others.forEach { other ->
                            if (other.uniqueId == entity.uniqueId) {
                                sender.sendLang("command-companion-add-self")
                                return@execute
                            }
                            entity.addCompanion(other)
                        }
                        sender.sendLang("command-companion-add-success", ctx["other-id"], entity.id)
                    }
                }
            }
        }
        // 移除伴生实体
        literal("remove") {
            dynamic("other-id") {
                suggestEntityList()
                execute<CommandSender> { sender, ctx, _ ->
                    val others = Command.finder.getEntitiesFromIdOrUniqueId(ctx["other-id"], sender as? Player).filter { !it.isDerived() }
                    if (others.isEmpty()) {
                        sender.sendLang("command-companion-not-found", ctx["other-id"])
                        return@execute
                    }
                    multiControl<EntitySource.Empty>(sender, ctx["id"], "companion-remove", unified = false) { entity ->
                        entity.removeCompanion(*others.toTypedArray())
                        sender.sendLang("command-companion-remove-success", ctx["other-id"], entity.id)
                    }
                }
            }
        }
        // 清空所有伴生实体
        literal("clear") {
            execute<CommandSender> { sender, ctx, _ ->
                multiControl<EntitySource.Empty>(sender, ctx["id"], "companion-clear", unified = false) { entity ->
                    val count = entity.getCompanions().size
                    entity.clearCompanions()
                    sender.sendLang("command-companion-clear-success", entity.id, count.toString())
                }
            }
        }
        // 设置宿主
        literal("host") {
            dynamic("host-id") {
                suggestEntityList()
                execute<CommandSender> { sender, ctx, _ ->
                    val hosts = Command.finder.getEntitiesFromIdOrUniqueId(ctx["host-id"], sender as? Player).filter { !it.isDerived() }
                    if (hosts.isEmpty()) {
                        sender.sendLang("command-companion-not-found", ctx["host-id"])
                        return@execute
                    }
                    if (hosts.size > 1) {
                        sender.sendLang("command-companion-host-multiple")
                        return@execute
                    }
                    val host = hosts.first()
                    multiControl<EntitySource.Empty>(sender, ctx["id"], "companion-host", unified = false) { entity ->
                        if (entity.uniqueId == host.uniqueId) {
                            sender.sendLang("command-companion-host-self")
                            return@execute
                        }
                        try {
                            entity.setHost(host)
                            sender.sendLang("command-companion-host-success", entity.id, host.id)
                        } catch (e: Exception) {
                            sender.sendLang("command-companion-host-failed", e.message ?: "Unknown error")
                        }
                    }
                }
            }
        }
        // 解除归属
        literal("unhost") {
            execute<CommandSender> { sender, ctx, _ ->
                multiControl<EntitySource.Empty>(sender, ctx["id"], "companion-unhost", unified = false) { entity ->
                    val host = entity.getHost()
                    if (host == null) {
                        sender.sendLang("command-companion-no-host", entity.id)
                        return@execute
                    }
                    entity.setHost(null)
                    sender.sendLang("command-companion-unhost-success", entity.id, host.id)
                }
            }
        }
        // 查看伴生关系信息
        literal("info") {
            execute<CommandSender> { sender, ctx, _ ->
                multiControl<EntitySource.Empty>(sender, ctx["id"], "companion-info", unified = false) { entity ->
                    sender.sendMessage("§5§l‹ ›§f §7伴生关系信息: §f${entity.id}")
                    sender.sendMessage("§5§l‹ ›§f §7  - UniqueId: §f${entity.uniqueId}")
                    
                    // 宿主信息
                    val host = entity.getHost()
                    if (host != null) {
                        sender.sendMessage("§5§l‹ ›§f §7  - 宿主: §f${host.id} §7(${host.uniqueId})")
                        val rootHost = entity.getRootHost()
                        if (rootHost != null && rootHost.uniqueId != host.uniqueId) {
                            sender.sendMessage("§5§l‹ ›§f §7  - 根宿主: §f${rootHost.id} §7(${rootHost.uniqueId})")
                        }
                    } else {
                        sender.sendMessage("§5§l‹ ›§f §7  - 宿主: §c无")
                    }
                    
                    // 伴生实体信息
                    val companions = entity.getCompanions()
                    if (companions.isNotEmpty()) {
                        sender.sendMessage("§5§l‹ ›§f §7  - 直接伴生实体 (${companions.size}):")
                        companions.forEach { companion ->
                            sender.sendMessage("§5§l‹ ›§f §7    - §f${companion.id} §7(${companion.uniqueId})")
                        }
                    } else {
                        sender.sendMessage("§5§l‹ ›§f §7  - 直接伴生实体: §c无")
                    }
                    
                    // 所有伴生实体（包括嵌套）
                    val allCompanions = entity.getAllCompanions()
                    if (allCompanions.size > companions.size) {
                        sender.sendMessage("§5§l‹ ›§f §7  - 所有伴生实体 (${allCompanions.size}):")
                        allCompanions.forEach { companion ->
                            val depth = calculateDepth(companion, entity)
                            val indent = "  ".repeat(depth)
                            sender.sendMessage("§5§l‹ ›§f §7    $indent- §f${companion.id} §7(${companion.uniqueId})")
                        }
                    }
                    
                    // 可见状态
                    sender.sendMessage("§5§l‹ ›§f §7  - 是否为伴生实体: §f${entity.isCompanion()}")
                    sender.sendMessage("§5§l‹ ›§f §7  - 可见玩家数: §f${entity.viewPlayers.visible.size}")
                }
            }
        }
    }
}

/**
 * 计算伴生实体的嵌套深度
 */
private fun calculateDepth(companion: ink.ptms.adyeshach.core.entity.EntityInstance, root: ink.ptms.adyeshach.core.entity.EntityInstance): Int {
    var depth = 1
    var current = companion.getHost()
    while (current != null && current.uniqueId != root.uniqueId) {
        depth++
        current = current.getHost()
    }
    return depth
}
