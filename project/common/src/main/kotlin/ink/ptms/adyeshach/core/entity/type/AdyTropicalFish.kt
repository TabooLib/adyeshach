package ink.ptms.adyeshach.core.entity.type

import org.bukkit.DyeColor
import org.bukkit.entity.TropicalFish

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyTropicalFish : AdyFish {

    fun getPatternColor(): DyeColor

    fun setPatternColor(value: DyeColor)

    fun getBodyColor(): DyeColor

    fun setBodyColor(value: DyeColor)

    fun getPattern(): TropicalFish.Pattern

    fun setPattern(value: TropicalFish.Pattern)

    fun getVariant(): Int {
        return getMetadata("variant")
    }

    fun setVariant(value: Int) {
        setMetadata("variant", value)
    }
}