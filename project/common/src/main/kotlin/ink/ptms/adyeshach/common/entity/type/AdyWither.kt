package ink.ptms.adyeshach.common.entity.type

import org.bukkit.entity.Entity

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
abstract class AdyWither : AdyMob() {

    open fun setFirstHeadTarget(value: Entity) {
        setMetadata("firstHeadTarget", value.entityId)
    }

    open fun setSecondHeadTarget(value: Entity) {
        setMetadata("secondHeadTarget", value.entityId)
    }

    open fun setThirdHeadTarget(value: Entity) {
        setMetadata("thirdHeadTarget", value.entityId)
    }

    open fun getInvulnerableTime(): Int {
        return getMetadata("invulnerableTime")
    }

    open fun setInvulnerableTime(value: Int) {
        setMetadata("invulnerableTime", value)
    }

    abstract fun getFirstHeadTarget(): Entity?

    abstract fun getSecondHeadTarget(): Entity?

    abstract fun getThirdHeadTarget(): Entity?
}