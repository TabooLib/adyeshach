package ink.ptms.adyeshach.module.command.subcommand

import ink.ptms.adyeshach.core.util.getEnumOrNull
import ink.ptms.adyeshach.core.util.sendLang
import ink.ptms.adyeshach.impl.entity.trait.TraitFactory
import ink.ptms.adyeshach.impl.entity.trait.impl.getTraitPatrolWaitTime
import ink.ptms.adyeshach.impl.entity.trait.impl.refreshTraitPatrolNodes
import ink.ptms.adyeshach.impl.entity.trait.impl.setTraitPatrolWaitTime
import ink.ptms.adyeshach.module.command.Command
import ink.ptms.adyeshach.module.command.EntitySource
import ink.ptms.adyeshach.module.command.multiControl
import ink.ptms.adyeshach.module.command.suggestEntityList
import ink.ptms.adyeshach.module.editor.*
import ink.ptms.adyeshach.module.editor.meta.MetaEditor
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggestUncheck
import taboolib.common5.cbool
import taboolib.common5.cint
import taboolib.common5.clong
import taboolib.module.nms.inputSign

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
                    // 基础面板
                    "main" -> editPanel.open(EditPanelType.MAIN, page)
                    "traits" -> editPanel.open(EditPanelType.TRAITS, page)
                    "public-meta" -> editPanel.open(EditPanelType.PUBLIC_META, page)
                    "private-meta" -> editPanel.open(EditPanelType.PRIVATE_META, page)
                    "move" -> editPanel.open(EditPanelType.MOVE, page)
                    // 控制器
                    "controller" -> PresetController.open(sender, entity)
                    // 可视化修改
                    "e" -> {
                        // 获取节点
                        val key = args.substringAfter(":").substringBefore("->")
                        // 默认值
                        val def = args.substringAfter("->").substringAfter(' ', "")
                        // 获取编辑器类型
                        val editType = EditType::class.java.getEnumOrNull(args.substringAfter("->").substringBefore(' ')) ?: EditType.AUTO
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
                                editor.open(entity, sender, def)
                            } else {
                                sender.sendLang("command-meta-not-support-editor", key)
                            }
                        } else {
                            // 固定类型
                            MetaEditor.getMetaEditor(editType, key).open(entity, sender, def)
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
                            if (value.equals("@RESET", true)) {
                                entity.setMetadata(metaFirst.key, metaFirst.def)
                            } else {
                                // 这里需要对 Masked 类型的元数据进行特殊兼容
                                // 因为 Masked 类型的元数据使用 ByteMetadataParser
                                if (metaFirst.def is Boolean) {
                                    entity.setMetadata(metaFirst.key, value.cbool)
                                } else {
                                    entity.setMetadata(metaFirst.key, metaFirst.getMetadataParser().parse(value))
                                }
                            }
                        }
                        // 设置自定义元数据
                        else if (!entity.setCustomMeta(key.replace("-", "_").lowercase(), value)) {
                            sender.sendLang("command-meta-not-found", key)
                        }
                    }
                    // 特性
                    "t" -> {
                        // 获取节点
                        when (val id = args.substringAfter(":")) {
                            // 基础特性
                            "sit", "title", "command", "view-condition" -> {
                                TraitFactory.getTraitById(id)?.edit(sender, entity)?.thenRun { ChatEditor.refresh(sender) }
                            }
                            // 巡逻
                            "patrols" -> {
                                TraitFactory.getTraitById("patrol")?.edit(sender, entity)
                            }
                            // 巡逻等待
                            "patrols-wait-time" -> {
                                val l1 = sender.lang("input-patrols-wait-time0")
                                val l2 = sender.lang("input-patrols-wait-time1")
                                // 牌子输入
                                sender.inputSign(arrayOf("${entity.getTraitPatrolWaitTime()}", "", l1, l2)) {
                                    entity.setTraitPatrolWaitTime(it[0].clong)
                                    ChatEditor.refresh(sender)
                                }
                            }
                            // 巡逻更新
                            "patrols-update" -> entity.refreshTraitPatrolNodes()
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
    execute<Player> { sender, _, _ ->
        multiControl<EntitySource.Empty>(sender, STANDARD_EDIT_TRACKER) { EditPanel(sender, it).open() }
    }
}