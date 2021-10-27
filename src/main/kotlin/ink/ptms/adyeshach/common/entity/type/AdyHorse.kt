package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.entity.Horse

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyHorse : AdyHorseBase(EntityTypes.HORSE) {

    init {
        testing = true
        /**
         * Variant (Color & Style)
         *
         * 1.15 -> 18
         * 1.14 -> 17
         * 1.10 -> 15
         * 1.9 -> 14
         */
//        natural(at(11700 to 19, 11500 to 18, 11400 to 17, 11000 to 15, 10900 to 14), "variant", 0)
//                .canEdit(false)
//                .build()
//        naturalEditor("horseColor")
//                .from(Editors.enums(Horse.Color::class) { _, entity, meta, _, e -> "/adyeshachapi edit horse_color ${entity.uniqueId} ${meta.key} $e" })
//                .reset { _, _ ->
//                    setColor(Horse.Color.WHITE)
//                }
//                .display { _, _, _ ->
//                    getColor().name
//                }.build()
//        naturalEditor("horseStyle")
//                .from(Editors.enums(Horse.Style::class) { _, entity, meta, _, e -> "/adyeshachapi edit horse_style ${entity.uniqueId} ${meta.key} $e" })
//                .reset { _, _ ->
//                    setStyle(Horse.Style.NONE)
//                }
//                .display { _, _, _ ->
//                    getStyle().name
//                }.build()
    }

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
            setMetadata("variant", color.ordinal and 255 or style.ordinal shl 8 and '\uff00'.code)
        }
    }

    companion object {

        private val hc = Horse.Color.values().size
        private val hs = Horse.Style.values().size
    }
}