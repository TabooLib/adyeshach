package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.AdyTropicalFish
import org.bukkit.DyeColor
import org.bukkit.entity.TropicalFish

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
class AdyTropicalFish(v2: ink.ptms.adyeshach.core.entity.EntityInstance):  AdyFish(EntityTypes.TROPICAL_FISH, v2) {

    fun getVariant(): Int {
        return getMetadata("variant")
    }

    fun setVariant(value: Int) {
        setMetadata("variant", value)
    }

    fun getPatternColor(): DyeColor {
        v2 as AdyTropicalFish
        return v2.getPatternColor()
    }

    fun setPatternColor(value: DyeColor) {
        v2 as AdyTropicalFish
        v2.setPatternColor(value)
    }

    fun getBodyColor(): DyeColor {
        v2 as AdyTropicalFish
        return v2.getBodyColor()
    }

    fun setBodyColor(value: DyeColor) {
        v2 as AdyTropicalFish
        v2.setBodyColor(value)
    }

    fun getPattern(): TropicalFish.Pattern {
        v2 as AdyTropicalFish
        return v2.getPattern()
    }

    fun setPattern(value: TropicalFish.Pattern) {
        v2 as AdyTropicalFish
        v2.setPattern(value)
    }
}