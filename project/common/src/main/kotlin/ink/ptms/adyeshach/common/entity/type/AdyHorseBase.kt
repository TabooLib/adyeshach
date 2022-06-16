package ink.ptms.adyeshach.common.entity.type

/**
 * @author sky
 * @since 2020-08-04 18:28
 */
abstract class AdyHorseBase : AdyEntityAgeable() {

    open fun setTamed(value: Boolean) {
        setMetadata("isTamed", value)
    }

    open fun isTamed(): Boolean {
        return getMetadata("isTamed")
    }

    open fun setSaddled(value: Boolean) {
        setMetadata("isSaddled", value)
    }

    open fun isSaddled(): Boolean {
        return getMetadata("isSaddled")
    }

    open fun setHasBred(value: Boolean) {
        setMetadata("hasBred", value)
    }

    open fun isHasBred(): Boolean {
        return getMetadata("hasBred")
    }

    open fun setEating(value: Boolean) {
        setMetadata("isEating", value)
    }

    open fun isEating(): Boolean {
        return getMetadata("isEating")
    }

    open fun setRearing(value: Boolean) {
        setMetadata("isRearing", value)
    }

    open fun isRearing(): Boolean {
        return getMetadata("isRearing")
    }

    open fun setMouthOpen(value: Boolean) {
        setMetadata("isMouthOpen", value)
    }

    open fun isMouthOpen(): Boolean {
        return getMetadata("isMouthOpen")
    }

    /**
     * 低版本方法
     */
    open fun setHasChest(value: Boolean) {
        assert(minecraftVersion >= 11300, "setHasChest", "DONKEY")
        setMetadata("hasChest", value)
    }

    /**
     * 低版本方法
     */
    open fun isHasChest(): Boolean {
        assert(minecraftVersion >= 11300, "isHasChest", "DONKEY")
        return getMetadata("hasChest")
    }
}