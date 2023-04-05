package ink.ptms.adyeshach.core.util

import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.chat.ComponentSerializer
import taboolib.module.chat.ComponentText
import taboolib.module.chat.RawMessage
import taboolib.module.nms.MinecraftVersion

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.util.Components
 *
 * @author 坏黑
 * @since 2023/2/3 23:52
 */
object Components {

    fun toLegacyText(value: String): String {
        return if (value.startsWith('{') && value.endsWith('}')) {
            TextComponent(*ComponentSerializer.parse(value)).toLegacyText()
        } else {
            value
        }
    }

    fun toRawMessage(value: Any): String {
        return if (MinecraftVersion.majorLegacy >= 11300) {
            when (value) {
                is TextComponent -> RawMessage().append(value.text).toRawMessage()
                is ComponentText -> value.toRawMessage()
                is RawMessage -> value.toRawMessage()
                else -> {
                    val str = value.toString()
                    if (str.startsWith('{') && str.endsWith('}')) {
                        str
                    } else {
                        RawMessage().append(str).toRawMessage()
                    }
                }
            }
        } else {
            when (value) {
                is TextComponent -> value.text
                is ComponentText -> value.toLegacyText()
                is RawMessage -> value.toLegacyText()
                else -> {
                    val str = value.toString()
                    if (str.startsWith('{') && str.endsWith('}')) {
                        TextComponent(*ComponentSerializer.parse(str)).toLegacyText()
                    } else {
                        str
                    }
                }
            }
        }
    }
}