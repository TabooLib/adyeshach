package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyWither() : AdyMob(EntityTypes.WITHER) {

    init {
        registerMeta(at(11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11), "firstHeadTarget", 0)
        registerMeta(at(11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "secondHeadTarget", 0)
        registerMeta(at(11500 to 17, 11400 to 16, 11000 to 14, 10900 to 13), "ThirdHeadTarget", 0)
        registerMeta(at(11500 to 18, 11400 to 17, 11000 to 15, 10900 to 14), "InvulnerableTime", 0)
    }
}