package ink.ptms.adyeshach.impl.entity.type

import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.AdyHorse
import ink.ptms.adyeshach.core.entity.type.minecraftVersion
import ink.ptms.adyeshach.core.util.getEnum
import ink.ptms.adyeshach.impl.util.ifTrue
import org.bukkit.entity.Horse

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.type.DefaultHorse
 *
 * @author 坏黑
 * @since 2022/6/29 19:04
 */
abstract class DefaultHorse(entityTypes: EntityTypes) : DefaultEntityLiving(entityTypes), AdyHorse {

    override fun setColorAndStyle(color: Horse.Color, style: Horse.Style) {
        if (minecraftVersion < 11600) {
            setMetadata("variant", color.ordinal and 255 or style.ordinal shl 8)
        } else {
            setMetadata("variant", color.ordinal and 255 or ((style.ordinal shl 8) and '\uff00'.code))
        }
    }

    override fun setColor(color: Horse.Color) {
        setColorAndStyle(color, getStyle())
    }

    override fun setStyle(style: Horse.Style) {
        setColorAndStyle(getColor(), style)
    }

    override fun getColor(): Horse.Color {
        return Horse.Color.values()[(getVariant() and 255) % hc]
    }

    override fun getStyle(): Horse.Style {
        return if (minecraftVersion < 11600) {
            Horse.Style.values()[(getVariant() ushr 8) % hs]
        } else {
            Horse.Style.values()[((getVariant() and '\uff00'.code) shr 8) % hs]
        }
    }

    override fun setCustomMeta(key: String, value: String?): Boolean {
        super.setCustomMeta(key, value).ifTrue { return true }
        return when (key) {
            "color" -> {
                setColor(if (value != null) Horse.Color::class.java.getEnum(value) else Horse.Color.WHITE)
                true
            }
            "style" -> {
                setStyle(if (value != null) Horse.Style::class.java.getEnum(value) else Horse.Style.WHITE)
                true
            }
            else -> false
        }
    }

    companion object {

        private val hc = Horse.Color.values().size
        private val hs = Horse.Style.values().size
    }
}