package ink.ptms.adyeshach.common.entity.type

import org.bukkit.DyeColor

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
abstract class AdySheep : AdyEntityAgeable() {

    open fun getDyeColor(): DyeColor {
        return DyeColor.getByWoolData(getMetadata("dyeColor")) ?: DyeColor.WHITE
    }

    open fun setDyeColor(dyeColor: DyeColor) {
        setMetadata("dyeColor", dyeColor.woolData)
    }

    open fun isSheared(): Boolean {
        return getMetadata("isSheared")
    }

    open fun setSheared(value: Boolean) {
        setMetadata("isSheared", value)
    }
}