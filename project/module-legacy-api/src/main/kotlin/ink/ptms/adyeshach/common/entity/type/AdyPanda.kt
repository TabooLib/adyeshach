package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 * 1.14+
 */
@Deprecated("Outdated but usable")
class AdyPanda(v2: ink.ptms.adyeshach.core.entity.EntityInstance):  AdyEntityAgeable(EntityTypes.PANDA, v2) {

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
        if (minecraftVersion < 11600) {
            error("Metadata \"isSitting\" not supported this minecraft version.")
        }
        return getMetadata("isSitting")
    }

    fun setSitting(value: Boolean) {
        if (minecraftVersion < 11600) {
            error("Metadata \"isSitting\" not supported this minecraft version.")
        }
        setMetadata("isSitting", value)
    }

    fun isOnBack(): Boolean {
        if (minecraftVersion < 11600) {
            error("Metadata \"isOnBack\" not supported this minecraft version.")
        }
        return getMetadata("isOnBack")
    }

    fun setIsOnBack(value: Boolean) {
        if (minecraftVersion < 11600) {
            error("Metadata \"isOnBack\" not supported this minecraft version.")
        }
        setMetadata("isOnBack", value)
    }
}