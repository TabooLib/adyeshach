package ink.ptms.adyeshach.core.entity.type

import org.bukkit.DyeColor

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdySheep : AdyEntityAgeable {

    fun getDyeColor(): DyeColor

    fun setDyeColor(dyeColor: DyeColor)

    fun isSheared(): Boolean

    fun setSheared(value: Boolean)
}