package ink.ptms.adyeshach.module.command

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.manager.ManagerType
import ink.ptms.adyeshach.module.editor.EditPanel
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.*
import taboolib.common.util.unsafeLazy
import taboolib.expansion.createHelper
import taboolib.platform.util.sendLang

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.command.Command
 *
 * @author 坏黑
 * @since 2022/12/17 20:47
 */
@CommandHeader(name = "adyeshach", aliases = ["ady", "npc"], permission = "adyeshach.command")
object Command {

    val finder by unsafeLazy { Adyeshach.api().getEntityFinder() }
    val manager by unsafeLazy { Adyeshach.api().getPublicEntityManager(ManagerType.PERSISTENT) }

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody
    val script = CommandScript

    @CommandBody
    val create = subCommand {
        dynamic("type") {
            suggest { EntityTypes.values().map { it.name } }
            dynamic("id") {
                dynamic("action") {
                    suggest { listOf("t", "target", "e", "edit") }
                    execute<Player> { sender, ctx, _ ->
                        // 快捷操作
                        when (ctx.argument(0)) {
                            // 生成在目标位置
                            "t", "target" -> {
                                val targetBlock = sender.getTargetBlock(hashSetOf(Material.AIR), 32)
                                val loc = if (targetBlock.type.isAir) {
                                    sender.location
                                } else {
                                    val worldAccess = Adyeshach.api().getMinecraftAPI().getWorldAccess()
                                    val blockAccess = worldAccess.createBlockAccess(sender.world, sender.location.chunk.x, sender.location.chunk.z)
                                    var blockHeight = blockAccess.getBlockHeight(targetBlock.x, targetBlock.y, targetBlock.z)
                                    if (blockHeight == 0.0) {
                                        blockHeight = 1.0
                                    }
                                    targetBlock.location.add(0.5, blockHeight, 0.5)
                                }
                                val npc = manager.create(EntityTypes.valueOf(ctx.argument(-2).uppercase()), loc)
                                npc.id = ctx.argument(-1)
                                sender.sendLang("command-create-success-1", npc.uniqueId)
                            }
                            // 打开编辑面板
                            "e", "edit" -> {
                                val npc = manager.create(EntityTypes.valueOf(ctx.argument(-2).uppercase()), sender.location)
                                npc.id = ctx.argument(-1)
                                EditPanel(sender, npc).open()
                            }
                        }
                    }
                }
                execute<Player> { sender, ctx, _ ->
                    val npc = manager.create(EntityTypes.valueOf(ctx.argument(-1).uppercase()), sender.location)
                    npc.id = ctx.argument(0)
                    sender.sendLang("command-create-success-1", npc.uniqueId)
                }
            }
        }
    }

    @CommandBody
    val remove = subCommand {
        dynamic("id") {
            suggestEntityList()
            dynamic("action") {
                suggestUncheck { listOf("a", "all") }
                execute<CommandSender> { sender, ctx, _ ->
                    val npcList = finder.getEntitiesFromIdOrUniqueId(ctx.argument(-1), if (sender is Player) sender else null)
                    if (npcList.isEmpty()) {
                        sender.sendLang("command-find-empty")
                        return@execute
                    }
                    // 删除单位
                    npcList.forEach { it.remove() }
                    // 打印列表
                    EntityGroup.check(sender)
                    // 提示信息
                    when (ctx.argument(0)) {
                        // 删除全部
                        "a", "all" -> sender.sendLang("command-remove-success-all", ctx.argument(-1), npcList.first().uniqueId)
                        // 删除单个
                        "c" -> sender.sendLang("command-remove-success", ctx.argument(-1), npcList.first().uniqueId)
                    }
                }
            }
            // 定向删除
            execute<CommandSender> { sender, ctx, _ ->
                multi(sender, ctx.argument(0), "remove") {
                    it.remove()
                    sender.sendLang("command-remove-success", it.id, it.uniqueId)
                }
            }
        }
        // 就近删除
        execute<Player> { sender, _, _ ->
            multi(sender, "remove")
        }
    }

    @CommandBody
    val edit = subCommand {
        dynamic("id") {
            suggestEntityList()
            dynamic("action") {
                suggestUncheck { listOf("a", "all") }
                execute<Player> { sender, ctx, _ ->
                    val npcList = finder.getEntitiesFromIdOrUniqueId(ctx.argument(-1), sender)
                    if (npcList.isEmpty()) {
                        sender.sendLang("command-find-empty")
                        return@execute
                    }

                }
            }
            // 定向编辑
            execute<Player> { sender, ctx, _ ->
                multi(sender, ctx.argument(0), "edit", all = false) {
                    EditPanel(sender, it).open()
                }
            }
        }
        // 就近编辑
        execute<Player> { sender, _, _ ->
            multi(sender, "edit")
        }
    }
}