package ink.ptms.adyeshach.core.entity.type

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

    @Deprecated("1.16 以下不支持")
    fun isSitting(): Boolean {
        assert(minecraftVersion < 11600, "isSitting")
        return getMetadata("isSitting")
    }

    @Deprecated("1.16 以下不支持")
    fun setSitting(value: Boolean) {
        assert(minecraftVersion < 11600, "setSitting")
        setMetadata("isSitting", value)
    }

    @Deprecated("1.16 以下不支持")
    fun isOnBack(): Boolean {
        assert(minecraftVersion < 11600, "isOnBack")
        return getMetadata("isOnBack")
    }

    @Deprecated("1.16 以下不支持")
    fun setIsOnBack(value: Boolean) {
        assert(minecraftVersion < 11600, "setIsOnBack")
        setMetadata("isOnBack", value)
    }

}