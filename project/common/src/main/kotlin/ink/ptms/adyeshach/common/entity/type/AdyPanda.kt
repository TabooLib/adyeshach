package ink.ptms.adyeshach.common.entity.type

/**
 * @author sky
 * @date 2020/8/4 23:15
 * 1.14+
 */
interface AdyPanda : AdyEntityAgeable {

    fun isSneezing(): Boolean {
        return getMetadata("isSneezing")
    }

    fun setSneezing(value: Boolean) {
        setMetadata("isSneezing", value)
    }

    fun isEating(): Boolean {
        return getMetadata("isEating")
    }

    fun setEating(value: Boolean) {
        setMetadata("isEating", value)
    }

    fun isSitting(): Boolean {
        assert(minecraftVersion < 11600, "isSitting")
        return getMetadata("isSitting")
    }

    fun setSitting(value: Boolean) {
        assert(minecraftVersion < 11600, "setSitting")
        setMetadata("isSitting", value)
    }

    fun isOnBack(): Boolean {
        assert(minecraftVersion < 11600, "isOnBack")
        return getMetadata("isOnBack")
    }

    fun setIsOnBack(value: Boolean) {
        assert(minecraftVersion < 11600, "setIsOnBack")
        setMetadata("isOnBack", value)
    }

}