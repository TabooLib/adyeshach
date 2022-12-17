package ink.ptms.adyeshach.module.editor

import ink.ptms.adyeshach.core.Adyeshach
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptCommandSender
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.chat.TellrawJson

const val LEFT_ARROW = "‹"

const val RIGHT_ARROW = "›"

/**
 * 清理玩家的聊天栏
 */
fun CommandSender.clearScreen() {
    TellrawJson().sendTo(adaptCommandSender(this)) { repeat(32) { newLine() } }
}

/**
 * 发送半屏消息
 */
fun Player.sendNativeHalfMessage(message: TellrawJson) {
    sendNativeFullMessage(message, 10)
}

/**
 * 发送全屏消息
 */
fun Player.sendNativeFullMessage(message: TellrawJson, maxLine: Int = 20) {
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
    return Adyeshach.api().getLanguage().getLang(this, "editor-$id", *args) ?: id
}