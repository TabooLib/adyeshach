package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @since 2020-08-04 19:30
 */
@Deprecated("Outdated but usable")
class AdyDolphin(v2: ink.ptms.adyeshach.core.entity.EntityInstance) : AdyMob(EntityTypes.DOLPHIN, v2) {

    fun setFindTreasure(value: Boolean) {
        setMetadata("findTreasure", value)
    }

    fun isFindTreasure(): Boolean {
        return getMetadata("findTreasure")
    }

    fun setHasFish(value: Boolean) {
        setMetadata("hasFish", value)
    }

    fun isHasFish(): Boolean {
        return getMetadata("hasFish")
    }
}