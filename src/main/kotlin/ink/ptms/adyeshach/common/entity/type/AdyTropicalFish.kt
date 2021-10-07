package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.DyeColor
import org.bukkit.entity.TropicalFish

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyTropicalFish : AdyFish(EntityTypes.TROPICAL_FISH) {

    fun getVariant(): Int {
        return getMetadata("variant")
    }

    fun setVariant(value: Int) {
        setMetadata("variant", value)
    }

    fun getPatternColor(): DyeColor {
        return getPatternColor(getVariant())
    }

    fun setPatternColor(value: DyeColor) {
        setVariant(getData(value, getBodyColor(), getPattern()))
    }

    fun getBodyColor(): DyeColor {
        return getBodyColor(getVariant())
    }

    fun setBodyColor(value: DyeColor) {
        setVariant(getData(getPatternColor(), value, getPattern()))
    }

    fun getPattern(): TropicalFish.Pattern {
        return getPattern(getVariant())
    }

    fun setPattern(value: TropicalFish.Pattern) {
        setVariant(getData(getPatternColor(), getBodyColor(), value))
    }

    private fun getData(patternColor: DyeColor, bodyColor: DyeColor, type: TropicalFish.Pattern): Int {
        return patternColor.woolData.toInt() shl 24 or (bodyColor.woolData.toInt() shl 16) or NMS.INSTANCE.getTropicalFishDataValue(type)
    }

    private fun getPatternColor(data: Int): DyeColor {
        return DyeColor.getByWoolData((data shr 24 and 255).toByte()) ?: DyeColor.WHITE
    }

    private fun getBodyColor(data: Int): DyeColor {
        return DyeColor.getByWoolData((data shr 16 and 255).toByte()) ?: DyeColor.WHITE
    }

    private fun getPattern(data: Int): TropicalFish.Pattern {
        return NMS.INSTANCE.getTropicalFishPattern(data and '\uffff'.code)
    }
}