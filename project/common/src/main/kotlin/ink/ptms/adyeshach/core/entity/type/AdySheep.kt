package ink.ptms.adyeshach.core.entity.type

import org.bukkit.DyeColor

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdySheep : AdyEntityAgeable {

    fun getDyeColor(): DyeColor {
        return DyeColor.getByWoolData(getMetadata("dyeColor")) ?: DyeColor.WHITE
    }

    fun setDyeColor(dyeColor: DyeColor) {
        setMetadata("dyeColor", dyeColor.woolData)
    }

    fun isSheared(): Boolean {
        return getMetadata("isSheared")
    }

    fun setSheared(value: Boolean) {
        setMetadata("isSheared", value)
    }
}