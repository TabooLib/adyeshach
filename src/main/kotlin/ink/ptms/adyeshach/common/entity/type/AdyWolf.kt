package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.editor.Editors
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.DyeColor

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyWolf : AdyEntityTameable(EntityTypes.WOLF) {

    init {
        registerMeta(at(11400 to 18, 11000 to 16, 10900 to 15), "isBegging", false)
        registerMeta(at(11400 to 19, 11000 to 17, 10900 to 16), "collarColor", DyeColor.RED.ordinal)
                .from(Editors.enums(DyeColor::class) { _, entity, meta, index, _ -> "/adyeshachapi edit int ${entity.uniqueId} ${meta.key} $index" })
                .display { _, entity, _ ->
                    DyeColor.values()[entity.getMetadata("collarColor")].name
                }.build()
    }

    fun setBegging(value: Boolean) {
        setMetadata("isBegging", value)
    }

    fun isBegging(): Boolean {
        return getMetadata("isBegging")
    }

    fun setCollarColor(value: DyeColor) {
        setMetadata("color", value.ordinal)
    }

    fun getCollarColor(): DyeColor {
        return DyeColor.values()[getMetadata("color")]
    }
}