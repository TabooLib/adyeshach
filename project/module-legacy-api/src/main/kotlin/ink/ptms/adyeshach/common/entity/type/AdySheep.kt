package ink.ptms.adyeshach.common.entity.type


import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.DyeColor

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
class AdySheep(v2: ink.ptms.adyeshach.core.entity.EntityInstance):  AdyEntityAgeable(EntityTypes.SHEEP, v2) {

    init {
        testing = true
    }

    @Suppress("DEPRECATION")
    fun getDyeColor(): DyeColor {
        return DyeColor.getByWoolData(getMetadata("dyeColor")) ?: DyeColor.WHITE
    }

    @Suppress("DEPRECATION")
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