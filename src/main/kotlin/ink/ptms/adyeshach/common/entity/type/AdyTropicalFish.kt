package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.editor.Editors
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.DyeColor
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftTropicalFish
import org.bukkit.entity.TropicalFish

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyTropicalFish : AdyFish(EntityTypes.TROPICAL_FISH) {

    init {
        registerMeta(at(11500 to 16, 11400 to 15, 11300 to 13), "variant", 0)
                .canEdit(false)
                .build()
        registerEditor("patternColor")
                .from(Editors.enums(DyeColor::class) { _, entity, meta, _, e -> "/adyeshachapi edit pattern_color ${entity.uniqueId} ${meta.key} $e" })
                .reset { entity, meta ->
                    setPatternColor(DyeColor.WHITE)
                }
                .display { _, entity, _ ->
                    getPatternColor().name
                }.build()
        registerEditor("bodyColor")
                .from(Editors.enums(DyeColor::class) { _, entity, meta, _, e -> "/adyeshachapi edit body_color ${entity.uniqueId} ${meta.key} $e" })
                .reset { entity, meta ->
                    setBodyColor(DyeColor.WHITE)
                }
                .display { _, entity, _ ->
                    getBodyColor().name
                }.build()
        registerEditor("pattern")
                .from(Editors.enums(TropicalFish.Pattern::class) { _, entity, meta, _, e -> "/adyeshachapi edit pattern ${entity.uniqueId} ${meta.key} $e" })
                .reset { entity, meta ->
                    setPattern(TropicalFish.Pattern.KOB)
                }
                .display { _, entity, _ ->
                    getPattern().name
                }.build()
    }

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
        return patternColor.woolData.toInt() shl 24 or (bodyColor.woolData.toInt() shl 16) or CraftTropicalFish.CraftPattern.values()[type.ordinal].dataValue
    }

    private fun getPatternColor(data: Int): DyeColor {
        return DyeColor.getByWoolData((data shr 24 and 255).toByte()) ?: DyeColor.WHITE
    }

    private fun getBodyColor(data: Int): DyeColor {
        return DyeColor.getByWoolData((data shr 16 and 255).toByte()) ?: DyeColor.WHITE
    }

    private fun getPattern(data: Int): TropicalFish.Pattern {
        return CraftTropicalFish.CraftPattern.fromData(data and '\uffff'.toInt())
    }
}