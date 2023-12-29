package ink.ptms.adyeshach.module.editor

import ink.ptms.adyeshach.core.Adyeshach
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptCommandSender
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.chat.RawMessage

const val LEFT_ARROW = "‹"

const val RIGHT_ARROW = "›"

/**
 * 清理玩家的聊天栏
 */
fun CommandSender.clearScreen() {
    RawMessage().sendTo(adaptCommandSender(this)) { repeat(32) { newLine() } }
}

/**
 * 发送半屏消息
 */
fun Player.sendNativeHalfMessage(message: RawMessage) {
    sendNativeFullMessage(message, 10)
}

/**
 * 发送全屏消息
 */
fun Player.sendNativeFullMessage(message: RawMessage, maxLine: Int = 20) {
    // 如果不足 maxLine 行则补全到 maxLine 行
    val lines = message.toLegacyText().lines()
    for (i in 0 until maxLine - lines.size) {
        message.newLine()
    }
    // 发送消息
    message.sendTo(adaptPlayer(this))
}

/**
 * 获取语言文件
 */
fun Player.lang(id: String, vararg args: Any): String {
    return Adyeshach.api().getLanguage().getLang(this, "editor-$id", *args)
        ?: Adyeshach.api().getLanguage().getLang(this, id, *args)
        ?: "§c{$id}"
}

fun String.toLocaleKey(): String {
    val builder = StringBuilder()
    toString().toCharArray().forEachIndexed { _, c ->
        when {
            c.isUpperCase() -> builder.append("-${c.lowercaseChar()}")
            else -> builder.append(c)
        }
    }
    return builder.toString()
}

fun Double.format(): String {
    return String.format("%.1f", this)
}

fun Float.format(): String {
    return String.format("%.1f", this)
}