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

    /**
     * 获取第一个脑袋的攻击目标
     */
    fun getFirstHeadTarget(): Entity?

    /**
     * 获取第二个脑袋的攻击目标
     */
    fun getSecondHeadTarget(): Entity?

    /**
     * 获取第三个脑袋的攻击目标
     */
    fun getThirdHeadTarget(): Entity?
}