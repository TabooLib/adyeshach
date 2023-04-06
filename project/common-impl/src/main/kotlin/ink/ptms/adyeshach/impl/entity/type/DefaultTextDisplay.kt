package ink.ptms.adyeshach.impl.entity.type

import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.AdyTextDisplay
import ink.ptms.adyeshach.impl.util.ifTrue
import ink.ptms.adyeshach.impl.util.toColor
import org.bukkit.entity.Display
import org.bukkit.entity.TextDisplay
import taboolib.common5.cint
import taboolib.module.chat.ComponentText
import taboolib.module.chat.Components

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.type.DefaultSheep
 *
 * @author 坏黑
 * @since 2023/1/10 00:25
 */
@Suppress("SpellCheckingInspection")
abstract class DefaultTextDisplay(entityTypes: EntityTypes) : DefaultDisplay(entityTypes), AdyTextDisplay {

    override fun setText(value: ComponentText) {
        setMetadata("text", value)
    }

    override fun getText(): ComponentText {
        val value = getMetadata<String>("text")
        return runCatching { Components.parseRaw(value) }.getOrElse { Components.text(value) }
    }

    override fun setAlignment(value: TextDisplay.TextAligment) {
        // 不知道 mojang 哪个小天才想出来这个设计，属实是真牛逼 👍
        when (value) {
            TextDisplay.TextAligment.CENTER -> {
                setMetadata("alignmentLeft", false)
                setMetadata("alignmentRight", false)
            }
            TextDisplay.TextAligment.LEFT -> {
                setMetadata("alignmentLeft", true)
                setMetadata("alignmentRight", false)
            }
            TextDisplay.TextAligment.RIGHT -> {
                setMetadata("alignmentLeft", false)
                setMetadata("alignmentRight", true)
            }
        }
    }

    override fun getAlignment(): TextDisplay.TextAligment {
        val left = getMetadata<Boolean>("alignmentLeft")
        val right = getMetadata<Boolean>("alignmentRight")
        return when {
            left && right -> TextDisplay.TextAligment.CENTER
            left -> TextDisplay.TextAligment.LEFT
            right -> TextDisplay.TextAligment.RIGHT
            else -> TextDisplay.TextAligment.CENTER
        }
    }

    override fun setCustomMeta(key: String, value: String?): Boolean {
        super.setCustomMeta(key, value).ifTrue { return true }
        return when (key) {
            "backgroundcolor", "background_color" -> {
                // 对 RGB 写法进行兼容
                if (value != null && value.contains(',')) {
                    setBackgroundColor(value.toColor())
                    true
                } else {
                    false
                }
            }
            else -> false
        }
    }
}