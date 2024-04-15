package ink.ptms.adyeshach.module.editor.page

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.module.editor.*
import ink.ptms.adyeshach.module.editor.action.ActionGroup
import ink.ptms.adyeshach.module.editor.meta.impl.MetaPrimitive
import taboolib.module.chat.colored
import taboolib.module.chat.uncolored

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.page.MultiplePage
 *
 * @author 坏黑
 * @since 2022/12/19 18:30
 */
abstract class MultiplePage(editor: EditPanel) : Page(editor) {

    abstract fun groups(): List<ActionGroup>

    override fun open(index: Int) {
        super.open(index)
        json.newLine()
        // 计算当前页需要显示的内容
        val groups = groups()
        var length = 0
        var cl = 0
        var p = 0
        for (group in groups) {
            // 本页不足以显示
            if (length + group.length() > 14) {
                length = 0
                p++
            }
            length += group.length()
            if (index == p) {
                cl = length
                send(group)
            }
        }
        // 填满 14 行
        for (i in cl..14) {
            json.newLine()
        }
        // 显示页码
        appendPage(p)
        // 发送全屏消息
        player.sendNativeFullMessage(json)
    }

    fun send(group: ActionGroup) {
        json.append("  &8${player.lang("group-${group.id()}")} ···".colored()).newLine()
        group.actions().forEachIndexed { index, action ->
            if (index % group.actionPerLine() == 0) {
                if (index > 0) {
                    json.newLine()
                }
                json.append("  ")
            }
            json.append("&8[".colored())
            // 展示文本
            json.append(action.display(player))
            // 描述
            val hoverText = arrayListOf<String>()
            val description = action.description(player)
            if (description != null) {
                hoverText += description.lines()
            }
            if (Adyeshach.api().getEntityMetadataRegistry().isUnusedMeta(entity, action.id())) {
                hoverText += player.lang("meta-unused").lines()
            }
            if (hoverText.isNotEmpty()) {
                json.hoverText(hoverText.joinToString("\n"))
            }
            // 点击命令
            if (action.isCustomCommand()) {
                val clickCommand = action.clickCommand(player, entity, this, index)?.uncolored()
                if (clickCommand != null) {
                    // 执行命令并刷新页面
                    if (action.isRefreshPage()) {
                        json.runCommand("/adyeshach api ee $clickCommand")
                    }
                    // 复制文本
                    else if (clickCommand.startsWith('>')) {
                        json.copyToClipboard(clickCommand.substring(1))
                    }
                    // 执行命令
                    else {
                        json.runCommand("/$clickCommand")
                    }
                }
            } else {
                json.runCommand("/adyeshach api ee adyeshach edit ${entity.uniqueId} e:${action.id()}->auto") // 使用 EDIT 并自动选择编辑器
            }
            // 重置
            if (action.isResettable()) {
                json.append(" ")
                json.append("&c(R)".colored())
                    .hoverText(player.lang("reset"))
                    .runCommand("/adyeshach api ee adyeshach edit ${entity.uniqueId} m:${action.id()}->@RESET") // 使用 MODIFY 重置数据
            }
            json.append("&8] ".colored())
        }
        if (group.actions().isNotEmpty()) {
            json.newLine()
        }
        json.newLine()
    }

    fun appendPage(maxPage: Int) {
        json.append("  ")
        json.appendLang("back").hoverText(player.lang("back-help")).runCommand("/adyeshach edit ${entity.uniqueId}")
        json.append(" ")
        // 上一页
        if (index > 0) {
            json.append("&8[ &7&l$LEFT_ARROW&8 ]".colored())
                .hoverText(player.lang("previous"))
                .runCommand("/adyeshach edit ${entity.uniqueId} ${subpage()}:${index - 1}")
        } else {
            json.append("&8[ &8&l$LEFT_ARROW&8 ]".colored())
        }
        // 当前页
        json.append("&f ${index + 1} / ${maxPage + 1} ".colored())
        // 下一页
        if (index < maxPage) {
            json.append("&8[ &7&l$RIGHT_ARROW&8 ]".colored())
                .hoverText(player.lang("next"))
                .runCommand("/adyeshach edit ${entity.uniqueId} ${subpage()}:${index + 1}")
        } else {
            json.append("&8[ &8&l${RIGHT_ARROW}&8 ]".colored())
        }
        // 偏好设置
        json.append(" ")
        when (MetaPrimitive.getPreferenceInputType(player)) {
            MetaPrimitive.InputType.SIGN -> {
                json.appendLang("input-primitive-type-sign")
                    .hoverLang("input-primitive-type-sign-warn")
                    .runCommand("/adyeshach api pi CHAT")
            }

            MetaPrimitive.InputType.CHAT -> {
                json.appendLang("input-primitive-type-chat")
                    .runCommand("/adyeshach api pi SIGN")
            }
        }
    }

    protected fun autoActionPerLine(): Int {
        return if (player.locale.startsWith("zh_")) 5 else 4
    }
}