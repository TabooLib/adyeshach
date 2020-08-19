package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyPrimedTNT() : AdyEntity(EntityTypes.PRIMED_TNT) {

    init {
        /*
        1.16, 1.15, 1.14
        7
        1.13, 1.12, 1.11, 1.10
        6
        1.9
        5
         */
        registerMeta(at(11400 to 7, 11000 to 6, 10900 to 5), "fuseTime", 80)
    }

    fun setFuseTime(fuseTime: Int) {
        setMetadata("fuseTime",fuseTime)
    }

    fun getFuseTime():Int{
        return getMetadata("fuseTime")
    }
}