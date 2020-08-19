package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.util.BukkitUtils
import org.bukkit.entity.Horse

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyHorse() : AdyHorseBase(EntityTypes.HORSE) {

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

    fun getVariant(): Int {
        return getMetadata("variant")
    }

    fun setColorAndStyle(color: Horse.Color, style: Horse.Style) {
        if (version < 11600) {
            throw RuntimeException("Horse's color and style does not supported this minecraft version. Use \"setVariant\" instead")
        }

        val colorIndex = BukkitUtils.valuesHorseColor().indexOf(color)
        val styleIndex = BukkitUtils.valuesHorseStyle().indexOf(style)

        return setMetadata("variant", colorIndex and 255 or styleIndex shl 8 and '\uff00'.toInt())
    }

    @Suppress("DEPRECATION")
    fun setVariant(variant: Horse.Variant) {
        setMetadata("variant", BukkitUtils.valuesHorseVariant().indexOf(variant))
    }

}