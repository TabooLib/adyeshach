package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes
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
        registerMetaByteMask(at(11500 to 18, 11400 to 17, 11000 to 15, 10900 to 14), "variant", 0)
    }

    fun getVariant(): Int {
        return getMetadata("variant")
    }

    fun setVariant(color: Horse.Color, style: Horse.Style) {
        // TODO
        return setMetadata("variant", 0)
    }

}