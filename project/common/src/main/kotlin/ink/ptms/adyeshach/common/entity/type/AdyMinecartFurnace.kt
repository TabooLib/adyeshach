package ink.ptms.adyeshach.common.entity.type

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyMinecartFurnace : AdyMinecart {

    fun isHasFuel(): Boolean {
        return getMetadata("hasFuel")
    }

    fun setFuel(hasFuel: Boolean) {
        setMetadata("hasFuel", hasFuel)
    }
}