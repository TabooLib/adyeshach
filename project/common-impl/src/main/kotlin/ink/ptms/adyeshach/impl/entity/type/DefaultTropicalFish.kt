package ink.ptms.adyeshach.impl.entity.type

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.AdyTropicalFish
import ink.ptms.adyeshach.core.util.getEnum
import ink.ptms.adyeshach.impl.util.ifTrue
import org.bukkit.DyeColor
import org.bukkit.entity.TropicalFish

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.type.DefaultTropicalFish
 *
 * @author 坏黑
 * @since 2022/6/29 19:06
 */
abstract class DefaultTropicalFish(entityTypes: EntityTypes) : DefaultEntityLiving(entityTypes), AdyTropicalFish {

    @Suppress("SpellCheckingInspection")
    override fun setCustomMeta(key: String, value: String): Boolean {
        super.setCustomMeta(key, value).ifTrue { return true }
        return when (key) {
            "bodycolor", "body_color" -> {
                setBodyColor(DyeColor::class.java.getEnum(value))
                true
            }
            "patterncolor", "pattern_color" -> {
                setPatternColor(DyeColor::class.java.getEnum(value))
                true
            }
            "pattern" -> {
                setPattern(TropicalFish.Pattern::class.java.getEnum(value))
                true
            }
            else -> false
        }
    }

    override fun getPatternColor(): DyeColor {
        return getPatternColor(getVariant())
    }

    override fun setPatternColor(value: DyeColor) {
        setVariant(getData(value, getBodyColor(), getPattern()))
    }

    override fun getBodyColor(): DyeColor {
        return getBodyColor(getVariant())
    }

    override fun setBodyColor(value: DyeColor) {
        setVariant(getData(getPatternColor(), value, getPattern()))
    }

    override fun getPattern(): TropicalFish.Pattern {
        return getPattern(getVariant())
    }

    override fun setPattern(value: TropicalFish.Pattern) {
        setVariant(getData(getPatternColor(), getBodyColor(), value))
    }

    protected fun getData(patternColor: DyeColor, bodyColor: DyeColor, type: TropicalFish.Pattern): Int {
        val helper = Adyeshach.api().getMinecraftAPI().getHelper()
        return patternColor.woolData.toInt() shl 24 or (bodyColor.woolData.toInt() shl 16) or helper.adaptTropicalFishPattern(type)
    }

    protected fun getPatternColor(data: Int): DyeColor {
        return DyeColor.getByWoolData((data shr 24 and 255).toByte()) ?: DyeColor.WHITE
    }

    protected fun getBodyColor(data: Int): DyeColor {
        return DyeColor.getByWoolData((data shr 16 and 255).toByte()) ?: DyeColor.WHITE
    }

    protected fun getPattern(data: Int): TropicalFish.Pattern {
        val helper = Adyeshach.api().getMinecraftAPI().getHelper()
        return helper.adaptTropicalFishPattern(data and '\uffff'.code)
    }
}