package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes
import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.entity.Horse

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyHorse() : AdyHorseBase(EntityTypes.HORSE) {

    @Expose
    private var color: Horse.Color = Horse.Color.WHITE

    @Expose
    private var style: Horse.Style = Horse.Style.NONE

    init {
        /**
         * Variant (Color & Style)
         *
         * 1.15 -> 18
         * 1.14 -> 17
         * 1.10 -> 15
         * 1.9 -> 14
         */
        registerMeta(at(11500 to 18, 11400 to 17, 11000 to 15, 10900 to 14), "variant", 0)
    }

    fun getColor(): Horse.Color {
        return color
    }

    fun getStyle(): Horse.Color {
        return color
    }

    fun setColor(color: Horse.Color) {
        this.color = color
        setColorAndStyle(this.color, this.style)
    }

    fun setStyle(style: Horse.Style) {
        this.style = style
        setColorAndStyle(this.color, this.style)
    }

    fun setColorAndStyle(color: Horse.Color, style: Horse.Style) {
        if (version < 11600) {
            setMetadata("variant", color.ordinal and 255 or style.ordinal shl 8)
        } else {
            setMetadata("variant", color.ordinal and 255 or style.ordinal shl 8 and '\uff00'.toInt())
        }
    }
}