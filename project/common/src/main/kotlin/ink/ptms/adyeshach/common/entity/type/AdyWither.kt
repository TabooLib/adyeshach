package ink.ptms.adyeshach.common.entity.type

import org.bukkit.entity.Entity

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyWither : AdyMob {

    fun setFirstHeadTarget(value: Entity) {
        setMetadata("firstHeadTarget", value.entityId)
    }

    fun setSecondHeadTarget(value: Entity) {
        setMetadata("secondHeadTarget", value.entityId)
    }

    fun setThirdHeadTarget(value: Entity) {
        setMetadata("thirdHeadTarget", value.entityId)
    }

    fun getInvulnerableTime(): Int {
        return getMetadata("invulnerableTime")
    }

    fun setInvulnerableTime(value: Int) {
        setMetadata("invulnerableTime", value)
    }

    fun getFirstHeadTarget(): Entity?

    fun getSecondHeadTarget(): Entity?

    fun getThirdHeadTarget(): Entity?
}