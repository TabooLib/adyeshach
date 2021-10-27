package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
abstract class AdyRaider(entityTypes: EntityTypes) : AdyMob(entityTypes) {

    init {
        /*
        1.16,1.15
        15
        1.14
        14
        1.13 -
         */
//        natural(at(11700 to 16, 11500 to 15, 11400 to 14), "isCelebrating", false)
    }

    fun isCelebrating(): Boolean {
        return getMetadata("isCelebrating")
    }

    fun setCelebrating(isCelebrating: Boolean) {
        setMetadata("isCelebrating", isCelebrating)
    }
}