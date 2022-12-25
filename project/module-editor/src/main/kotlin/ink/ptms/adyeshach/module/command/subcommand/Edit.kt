package ink.ptms.adyeshach.module.command.subcommand

import ink.ptms.adyeshach.module.command.Command
import ink.ptms.adyeshach.module.command.EntitySource
import ink.ptms.adyeshach.module.command.multiControl
import ink.ptms.adyeshach.module.command.suggestEntityList
import ink.ptms.adyeshach.module.editor.EditPanel
import ink.ptms.adyeshach.module.editor.EditPanelType
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggestUncheck
import taboolib.common5.cint
import taboolib.platform.util.sendLang

const val STANDARD_EDIT_TRACKER = "edit"

/**
 * npc edit (action)?
 *
 * npc edit e:hand->RESET
 */
val editSubCommand = subCommand {
    dynamic("id") {
        suggestEntityList()
        dynamic("action") {
            suggestUncheck { listOf("m", "main", "t", "traits", "pm", "public-meta", "pr", "private-meta", "move", "e") }
            execute<Player> { sender, ctx, args ->
                val npcList = Command.finder.getEntitiesFromIdOrUniqueId(ctx.argument(-1), sender)
                if (npcList.isEmpty()) {
                    sender.sendLang("command-find-empty")
                    return@execute
                }
                val editPanel = EditPanel(sender, npcList.first())
                val page = args.substringAfter(":").cint
                when (args.substringBefore(":")) {
                    "m", "main" -> editPanel.open(EditPanelType.MAIN, page)
                    "t", "traits" -> editPanel.open(EditPanelType.TRAITS, page)
                    "pm", "public-meta" -> editPanel.open(EditPanelType.PUBLIC_META, page)
                    "pr", "private-meta" -> editPanel.open(EditPanelType.PRIVATE_META, page)
                    "move" -> editPanel.open(EditPanelType.MOVE, page)
                    "e" -> {
                        val key = args.substringAfter(":").substringBefore("->")
                        val value = args.substringAfter("->")
                        npcList.forEach { entity ->
                            val metaFirst = entity.getAvailableEntityMeta().firstOrNull { it.key.equals(key, true) }
                            if (metaFirst != null) {
                                if (value == "@RESET") {
                                    entity.setMetadata(metaFirst.key, metaFirst.def)
                                } else {
                                    entity.setMetadata(metaFirst.key, metaFirst.getMetadataParser().parse(value))
                                }
                            } else if (!entity.setCustomMeta(key, value)) {
                                sender.sendLang("command-meta-not-found", key)
                            }
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