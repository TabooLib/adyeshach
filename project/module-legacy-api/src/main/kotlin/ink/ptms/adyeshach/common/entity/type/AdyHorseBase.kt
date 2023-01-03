package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @since 2020-08-04 18:28
 */
@Deprecated("Outdated but usable")
abstract class AdyHorseBase(entityTypes: EntityTypes, v2: ink.ptms.adyeshach.core.entity.EntityInstance) : AdyEntityAgeable(entityTypes, v2) {

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

    open fun setHasChest(value: Boolean) {
        if (minecraftVersion >= 11300) {
            error("Metadata \"hasChest\" not supported this minecraft version.")
        }
        setMetadata("hasChest", value)
    }

    open fun isHasChest(): Boolean {
        if (minecraftVersion >= 11300) {
            error("Metadata \"hasChest\" not supported this minecraft version.")
        }
        return getMetadata("hasChest")
    }
}