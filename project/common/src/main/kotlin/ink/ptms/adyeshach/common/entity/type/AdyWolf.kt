package ink.ptms.adyeshach.common.entity.type

import org.bukkit.DyeColor

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyWolf : AdyEntityTameable {

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