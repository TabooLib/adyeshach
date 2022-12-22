package ink.ptms.adyeshach.module.command.subcommand

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.module.command.Command
import ink.ptms.adyeshach.module.editor.EditPanel
import org.bukkit.Material
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggest
import taboolib.platform.util.sendLang

val createSubCommand = subCommand {
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
                            val npc = Command.manager.create(EntityTypes.valueOf(ctx.argument(-2).uppercase()), loc)
                            npc.id = ctx.argument(-1)
                            sender.sendLang("command-create-success-1", npc.uniqueId)
                        }
                        // 打开编辑面板
                        "e", "edit" -> {
                            val npc = Command.manager.create(EntityTypes.valueOf(ctx.argument(-2).uppercase()), sender.location)
                            npc.id = ctx.argument(-1)
                            EditPanel(sender, npc).open()
                        }
                    }
                }
            }
            execute<Player> { sender, ctx, _ ->
                val npc = Command.manager.create(EntityTypes.valueOf(ctx.argument(-1).uppercase()), sender.location)
                npc.id = ctx.argument(0)
                sender.sendLang("command-create-success-1", npc.uniqueId)
            }
        }
    }
}