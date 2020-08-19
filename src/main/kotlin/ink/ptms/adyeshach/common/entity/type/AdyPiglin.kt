package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyPiglin() : AdyEntityLiving(EntityTypes.PIGLIN) {

    init {
        val index = at(11600 to 15)
        registerMeta(index + 0, "isImmuneToZombification", false)
        registerMeta(index + 1, "isBaby", false)
        registerMeta(index + 2, "isChargingCrossbow", false)
        registerMeta(index + 3, "isDancing", false)
    }
}