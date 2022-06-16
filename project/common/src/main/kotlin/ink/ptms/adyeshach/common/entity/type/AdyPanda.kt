package ink.ptms.adyeshach.common.entity.type

/**
 * @author sky
 * @date 2020/8/4 23:15
 * 1.14+
 */
abstract class AdyPanda : AdyEntityAgeable() {

    open fun isSneezing(): Boolean {
        return getMetadata("isSneezing")
    }

    open fun setSneezing(value: Boolean) {
        setMetadata("isSneezing", value)
    }

    open fun isEating(): Boolean {
        return getMetadata("isEating")
    }

    open fun setEating(value: Boolean) {
        setMetadata("isEating", value)
    }

    open fun isSitting(): Boolean {
        assert(minecraftVersion < 11600, "isSitting")
        return getMetadata("isSitting")
    }

    open fun setSitting(value: Boolean) {
        assert(minecraftVersion < 11600, "setSitting")
        setMetadata("isSitting", value)
    }

    open fun isOnBack(): Boolean {
        assert(minecraftVersion < 11600, "isOnBack")
        return getMetadata("isOnBack")
    }

    open fun setIsOnBack(value: Boolean) {
        assert(minecraftVersion < 11600, "setIsOnBack")
        setMetadata("isOnBack", value)
    }

}