package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.entity.Entity

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyWither : AdyMob(EntityTypes.WITHER) {

    init {
//        registerMeta(at(11700 to 16, 11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11), "firstHeadTarget", 0)
//                .canEdit(false)
//                .build()
//        registerMeta(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "secondHeadTarget", 0)
//                .canEdit(false)
//                .build()
//        registerMeta(at(11700 to 18, 11500 to 17, 11400 to 16, 11000 to 14, 10900 to 13), "thirdHeadTarget", 0)
//                .canEdit(false)
//                .build()
//        registerMeta(at(11700 to 19, 11500 to 18, 11400 to 17, 11000 to 15, 10900 to 14), "invulnerableTime", 0)
    }

    fun getFirstHeadTarget(): Entity? {
        return NMS.INSTANCE.getEntity(position.toLocation().world!!, getMetadata("firstHeadTarget"))
    }

    fun setFirstHeadTarget(value: Entity) {
        setMetadata("firstHeadTarget", value.entityId)
    }

    fun getSecondHeadTarget(): Entity? {
        return NMS.INSTANCE.getEntity(position.toLocation().world!!, getMetadata("secondHeadTarget"))
    }

    fun setSecondHeadTarget(value: Entity) {
        setMetadata("secondHeadTarget", value.entityId)
    }

    fun getThirdHeadTarget(): Entity? {
        return NMS.INSTANCE.getEntity(position.toLocation().world!!, getMetadata("thirdHeadTarget"))
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