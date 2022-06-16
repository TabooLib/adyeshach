package ink.ptms.adyeshach.common.entity.type

import org.bukkit.DyeColor
import org.bukkit.entity.TropicalFish

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
abstract class AdyTropicalFish : AdyFish() {

    open fun getVariant(): Int {
        return getMetadata("variant")
    }

    open fun setVariant(value: Int) {
        setMetadata("variant", value)
    }

    open fun getPatternColor(): DyeColor {
        return getPatternColor(getVariant())
    }

    open fun setPatternColor(value: DyeColor) {
        setVariant(getData(value, getBodyColor(), getPattern()))
    }

    open fun getBodyColor(): DyeColor {
        return getBodyColor(getVariant())
    }

    open fun setBodyColor(value: DyeColor) {
        setVariant(getData(getPatternColor(), value, getPattern()))
    }

    open fun getPattern(): TropicalFish.Pattern {
        return getPattern(getVariant())
    }

    open fun setPattern(value: TropicalFish.Pattern) {
        setVariant(getData(getPatternColor(), getBodyColor(), value))
    }

    protected abstract fun getData(patternColor: DyeColor, bodyColor: DyeColor, type: TropicalFish.Pattern): Int

    protected abstract fun getPatternColor(data: Int): DyeColor

    protected abstract fun getBodyColor(data: Int): DyeColor

    protected abstract fun getPattern(data: Int): TropicalFish.Pattern
}