package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyMinecartFurnace : AdyMinecart(EntityTypes.MINECART_FURNACE) {

    init {
        /*
        1.16,1.15,1.14
        13 ->Has fuel
        1.13,1.12,1.11,1.10
        12 ->Has fuel
        1.9
        11 ->Has fuel
         */
//        natural(at(11700 to 14, 11400 to 13, 11000 to 12, 10900 to 11), "hasFuel", false)
    }

    fun isHasFuel(): Boolean {
        return getMetadata("hasFuel")
    }

    fun setFuel(hasFuel: Boolean) {
        setMetadata("hasFuel", hasFuel)
    }
}