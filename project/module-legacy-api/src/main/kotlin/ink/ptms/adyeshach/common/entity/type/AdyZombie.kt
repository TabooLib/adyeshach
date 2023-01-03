package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @since 2020-08-04 18:28
 */
@Deprecated("Outdated but usable")
open class AdyZombie(entityTypes: EntityTypes, v2: ink.ptms.adyeshach.core.entity.EntityInstance) : AdyMob(entityTypes, v2) {

    constructor(v2: ink.ptms.adyeshach.core.entity.EntityInstance) : this(EntityTypes.ZOMBIE, v2)

    fun setBaby(value: Boolean) {
        setMetadata("isBaby", value)
    }

    fun isBaby(): Boolean {
        return getMetadata("isBaby")
    }

    fun setDrowning(value: Boolean) {
        setMetadata("isDrowning", value)
    }

    fun isDrowning(): Boolean {
        return getMetadata("isDrowning")
    }

    fun setHandsHeldUp(value: Boolean) {
        setMetadata("isHandsHeldUp", value)
    }

    fun getHandsHeldUp(): Boolean {
        return getMetadata("isHandsHeldUp")
    }

    fun setZombieType(value: Int) {
        setMetadata("zombieType", value)
    }

    fun getZombieType(): Int {
        return getMetadata("zombieType")
    }

    fun setConverting(value: Boolean) {
        setMetadata("isConverting", value)
    }

    fun isConverting(): Boolean {
        return getMetadata("isConverting")
    }

    var isBecomingDrowned: Boolean
        get() = getMetadata("isBecomingDrowned")
        set(value) {
            setMetadata("isBecomingDrowned", value)
        }
}