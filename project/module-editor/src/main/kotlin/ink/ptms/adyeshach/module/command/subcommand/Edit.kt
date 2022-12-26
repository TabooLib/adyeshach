package ink.ptms.adyeshach.module.command.subcommand

import ink.ptms.adyeshach.core.util.getEnumOrNull
import ink.ptms.adyeshach.module.command.Command
import ink.ptms.adyeshach.module.command.EntitySource
import ink.ptms.adyeshach.module.command.multiControl
import ink.ptms.adyeshach.module.command.suggestEntityList
import ink.ptms.adyeshach.module.editor.EditPanel
import ink.ptms.adyeshach.module.editor.EditPanelType
import ink.ptms.adyeshach.module.editor.EditType
import ink.ptms.adyeshach.module.editor.meta.MetaEditor
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggestUncheck
import taboolib.common5.cint
import taboolib.platform.util.sendLang

const val STANDARD_EDIT_TRACKER = "edit"

/**
 * npc edit (action)?
 *
 * npc edit e:nitwit->m
 * npc edit e:nitwit->r
 * npc edit m:hand->RESET
 */
val editSubCommand = subCommand {
    dynamic("id") {
        suggestEntityList()
        dynamic("action") {
            suggestUncheck { listOf("main", "traits", "public-meta", "private-meta", "move") }
            execute<Player> { sender, ctx, args ->
                val npcList = Command.finder.getEntitiesFromIdOrUniqueId(ctx.argument(-1), sender)
                if (npcList.isEmpty()) {
                    sender.sendLang("command-find-empty")
                    return@execute
                }
                val entity = npcList.first()
                val editPanel = EditPanel(sender, entity)
                // 页码
                val page = args.substringAfter(":").cint
                // 类型
                when (args.substringBefore(":")) {
                    "main" -> editPanel.open(EditPanelType.MAIN, page)
                    "traits" -> editPanel.open(EditPanelType.TRAITS, page)
                    "public-meta" -> editPanel.open(EditPanelType.PUBLIC_META, page)
                    "private-meta" -> editPanel.open(EditPanelType.PRIVATE_META, page)
                    "move" -> editPanel.open(EditPanelType.MOVE, page)
                    // 可视化修改
                    "e" -> {
                        // 获取节点
                        val key = args.substringAfter(":").substringBefore("->")
                        // 获取编辑器类型
                        val editType = EditType::class.java.getEnumOrNull(args.substringAfter("->")) ?: EditType.AUTO
                        // 自动识别
                        if (editType == EditType.AUTO) {
                            // 优先获取自定义编辑器
                            var editor = MetaEditor.getCustomMetaEditor(entity, key)
                            if (editor == null) {
                                // 再获取默认编辑器
                                val metaFirst = entity.getAvailableEntityMeta().firstOrNull { it.key.equals(key, true) }
                                if (metaFirst != null) {
                                    editor = MetaEditor.getMetaEditor(metaFirst)
                                }
                            }
                            // 打开编辑器
                            if (editor != null) {
                                editor.open(entity, sender)
                            } else {
                                sender.sendLang("command-meta-not-support-editor", key)
                            }
                        } else {
                            // 固定类型
                            MetaEditor.getMetaEditor(editType, key).open(entity, sender)
                        }
                    }
                    // 快速修改
                    "m" -> {
                        // 获取节点
                        val key = args.substringAfter(":").substringBefore("->")
                        // 获取值
                        val value = args.substringAfter("->")
                        // 获取有效的实体元数据
                        val metaFirst = entity.getAvailableEntityMeta().firstOrNull { it.key.equals(key, true) }
                        if (metaFirst != null) {
                            if (value == "@RESET") {
                                entity.setMetadata(metaFirst.key, metaFirst.def)
                            } else {
                                entity.setMetadata(metaFirst.key, metaFirst.getMetadataParser().parse(value))
                            }
                        }
                        // 设置自定义元数据
                        else if (!entity.setCustomMeta(key, value)) {
                            sender.sendLang("command-meta-not-found", key)
                        }
                    }
                }
            }
        }
        // 定向编辑
        execute<Player> { sender, ctx, _ ->
            multiControl<EntitySource.Empty>(sender, ctx.argument(0), STANDARD_EDIT_TRACKER, unified = false) { EditPanel(sender, it).open() }
        }
    }
    // 就近编辑
    execute<Player> { sender, _, _ -> multiControl<EntitySource.Empty>(sender, STANDARD_EDIT_TRACKER) }
}