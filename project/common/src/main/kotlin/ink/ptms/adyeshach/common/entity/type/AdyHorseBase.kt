package ink.ptms.adyeshach.common.entity.type

/**
 * @author sky
 * @since 2020-08-04 18:28
 */
interface AdyHorseBase : AdyEntityAgeable {

    fun setTamed(value: Boolean) {
        setMetadata("isTamed", value)
    }

    fun isTamed(): Boolean {
        return getMetadata("isTamed")
    }

    fun setSaddled(value: Boolean) {
        setMetadata("isSaddled", value)
    }

    fun isSaddled(): Boolean {
        return getMetadata("isSaddled")
    }

    fun setHasBred(value: Boolean) {
        setMetadata("hasBred", value)
    }

    fun isHasBred(): Boolean {
        return getMetadata("hasBred")
    }

    fun setEating(value: Boolean) {
        setMetadata("isEating", value)
    }

    fun isEating(): Boolean {
        return getMetadata("isEating")
    }

    fun setRearing(value: Boolean) {
        setMetadata("isRearing", value)
    }

    fun isRearing(): Boolean {
        return getMetadata("isRearing")
    }

    fun setMouthOpen(value: Boolean) {
        setMetadata("isMouthOpen", value)
    }

    fun isMouthOpen(): Boolean {
        return getMetadata("isMouthOpen")
    }

    /**
     * 低版本方法
     */
    @Deprecated("1.13 以上不支持")
    fun setHasChest(value: Boolean) {
        assert(minecraftVersion >= 11300, "setHasChest", "DONKEY")
        setMetadata("hasChest", value)
    }

    /**
     * 低版本方法
     */
    @Deprecated("1.13 以上不支持")
    fun isHasChest(): Boolean {
        assert(minecraftVersion >= 11300, "isHasChest", "DONKEY")
        return getMetadata("hasChest")
    }
}