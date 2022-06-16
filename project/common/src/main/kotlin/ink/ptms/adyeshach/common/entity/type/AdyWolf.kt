package ink.ptms.adyeshach.common.entity.type

import org.bukkit.DyeColor

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
abstract class AdyWolf : AdyEntityTameable() {

    open fun setBegging(value: Boolean) {
        setMetadata("isBegging", value)
    }

    open fun isBegging(): Boolean {
        return getMetadata("isBegging")
    }

    open fun setCollarColor(value: DyeColor) {
        setMetadata("color", value.ordinal)
    }

    open fun getCollarColor(): DyeColor {
        return DyeColor.values()[getMetadata("color")]
    }
}