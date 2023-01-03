package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.AdyWither
import org.bukkit.entity.Entity

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
class AdyWither(v2: ink.ptms.adyeshach.core.entity.EntityInstance):  AdyMob(EntityTypes.WITHER, v2) {

    fun getFirstHeadTarget(): Entity? {
        v2 as AdyWither
        return v2.getFirstHeadTarget()
    }

    fun setFirstHeadTarget(value: Entity) {
        setMetadata("firstHeadTarget", value.entityId)
    }

    fun getSecondHeadTarget(): Entity? {
        v2 as AdyWither
        return v2.getSecondHeadTarget()
    }

    fun setSecondHeadTarget(value: Entity) {
        setMetadata("secondHeadTarget", value.entityId)
    }

    fun getThirdHeadTarget(): Entity? {
        v2 as AdyWither
        return v2.getThirdHeadTarget()
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
}