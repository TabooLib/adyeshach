package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.Bukkit
import org.bukkit.entity.Entity

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyWither() : AdyMob(EntityTypes.WITHER) {

    init {
        registerMeta(at(11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11), "firstHeadTarget", 0)
        registerMeta(at(11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "secondHeadTarget", 0)
        registerMeta(at(11500 to 17, 11400 to 16, 11000 to 14, 10900 to 13), "thirdHeadTarget", 0)
        registerMeta(at(11500 to 18, 11400 to 17, 11000 to 15, 10900 to 14), "invulnerableTime", 0)
    }

    fun getFirstHeadTarget(): Entity? {
        return Bukkit.getEntity(getMetadata("firstHeadTarget"))
    }

    fun setFirstHeadTarget(firstHeadTarget: Entity) {
        setMetadata("firstHeadTarget", firstHeadTarget.entityId)
    }

    fun getSecondHeadTarget(): Entity? {
        //TODO get entity from entityId
        return Bukkit.getEntity(getMetadata("secondHeadTarget"))
    }

    fun setSecondHeadTarget(secondHeadTarget: Entity) {
        setMetadata("secondHeadTarget", secondHeadTarget.entityId)
    }

    //TODO more setter & getter
}