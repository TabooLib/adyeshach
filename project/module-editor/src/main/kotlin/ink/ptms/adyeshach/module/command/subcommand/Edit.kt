package ink.ptms.adyeshach.module.command.subcommand

import ink.ptms.adyeshach.core.util.getEnumOrNull
import ink.ptms.adyeshach.core.util.sendLang
import ink.ptms.adyeshach.impl.entity.trait.TraitFactory
import ink.ptms.adyeshach.impl.entity.trait.impl.*
import ink.ptms.adyeshach.module.command.Command
import ink.ptms.adyeshach.module.command.EntitySource
import ink.ptms.adyeshach.module.command.multiControl
import ink.ptms.adyeshach.module.command.suggestEntityList
import ink.ptms.adyeshach.module.editor.*
import ink.ptms.adyeshach.module.editor.controller.PresetController
import ink.ptms.adyeshach.module.editor.meta.MetaEditor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggestUncheck
import taboolib.common5.cbool
import taboolib.common5.cdouble
import taboolib.common5.cint
import taboolib.common5.clong
import taboolib.module.chat.colored
import taboolib.module.nms.inputSign

private const val STANDARD_EDIT_TRACKER = "edit"

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
            execute<CommandSender> { sender, ctx, args ->
                val npcList = Command.finder.getEntitiesFromIdOrUniqueId(ctx["id"], sender as? Player)
                if (npcList.isEmpty()) {
                    sender.sendLang("command-find-empty")
                    return@execute
                }
                val entity = npcList.first()
                // 类型
                val type = args.substringBefore(":")
                // 页码
                val page = args.substringAfter(":").cint
                when {
                    // 基础面板
                    sender is Player && type == "main" -> EditPanel(sender, entity).open(EditPanelType.MAIN, page)
                    sender is Player && type == "traits" -> EditPanel(sender, entity).open(EditPanelType.TRAITS, page)
                    sender is Player && type == "public-meta" -> EditPanel(sender, entity).open(EditPanelType.PUBLIC_META, page)
                    sender is Player && type == "private-meta" -> EditPanel(sender, entity).open(EditPanelType.PRIVATE_META, page)
                    sender is Player && type == "move" -> EditPanel(sender, entity).open(EditPanelType.MOVE, page)
                    // 控制器
                    sender is Player && type == "controller" -> PresetController.open(sender, entity)
                    // 可视化修改
                    sender is Player && type == "e" -> {
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
                    type == "m" -> {
                        // 获取节点
                        val key = args.substringAfter(":").substringBefore("->")
                        // 获取值
                        val value = args.substringAfter("->").colored()
                        // 设置自定义元数据
                        if (!entity.setCustomMeta(key.replace("-", "_").lowercase(), if (value != "@RESET") value else null)) {
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
                            } else {
                                sender.sendLang("command-meta-not-found", key)
                            }
                        }
                    }
                    // 特性
                    sender is Player && type == "t" -> {
                        // 获取节点
                        when (val id = args.substringAfter(":")) {
                            // 基础特性
                            "sit", "title", "command", "view-condition" -> {
                                TraitFactory.getTraitById(id)?.edit(sender, entity)?.thenRun { ChatEditor.refresh(sender) }
                            }
                            // 头衔高度
                            "title-height" -> {
                                // 牌子输入
                                sender.inputSign(arrayOf("${entity.getTraitTitleHeight()}", "", sender.lang("input-title-height"), "")) {
                                    entity.setTraitTitleHeight(it[0].cdouble)
                                    ChatEditor.refresh(sender)
                                }
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
            multiControl<EntitySource.Empty>(sender, ctx.self(), STANDARD_EDIT_TRACKER, unified = false) { EditPanel(sender, it).open() }
        }
    }
    // 就近编辑
    execute<Player> { sender, _, _ ->
        val nearestEntity = Command.finder.getNearestEntity(sender) { !it.isDerived() && !it.isUneditable() }
        if (nearestEntity == null) {
            sender.sendLang("command-find-empty")
        } else {
            EditPanel(sender, nearestEntity).open()
        }
    }
}