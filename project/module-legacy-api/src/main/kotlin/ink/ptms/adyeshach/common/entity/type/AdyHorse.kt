package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.entity.Horse

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
class AdyHorse(v2: ink.ptms.adyeshach.core.entity.EntityInstance):  AdyHorseBase(EntityTypes.HORSE, v2) {

    fun getVariant(): Int {
        return getMetadata("variant")
    }

    fun getColor(): Horse.Color {
        return Horse.Color.values()[(getVariant() and 255) % hc]
    }

    fun getStyle(): Horse.Style {
        return if (minecraftVersion < 11600) {
            Horse.Style.values()[(getVariant() ushr 8) % hs]
        } else {
            Horse.Style.values()[((getVariant() and '\uff00'.code) shr 8) % hs]
        }
    }

    fun setColor(color: Horse.Color) {
        setColorAndStyle(color, getStyle())
    }

    fun setStyle(style: Horse.Style) {
        setColorAndStyle(getColor(), style)
    }

    fun setColorAndStyle(color: Horse.Color, style: Horse.Style) {
        if (minecraftVersion < 11600) {
            setMetadata("variant", color.ordinal and 255 or style.ordinal shl 8)
        } else {
            setMetadata("variant", color.ordinal and 255 or ((style.ordinal shl 8) and '\uff00'.code))
        }
    }

    companion object {

        private val hc = Horse.Color.values().size
        private val hs = Horse.Style.values().size
    }
}