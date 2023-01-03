package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.DyeColor

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
class AdyWolf(v2: ink.ptms.adyeshach.core.entity.EntityInstance):  AdyEntityTameable(EntityTypes.WOLF, v2) {

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