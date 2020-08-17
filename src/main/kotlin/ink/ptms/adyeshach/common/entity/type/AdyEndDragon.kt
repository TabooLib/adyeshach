package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyEndDragon() : AdyEntityLiving(EntityTypes.ENDER_DRAGON) {

    init {
        /*

        1.16,1.15
        15 ->Dragon phase
        1.14
        14 ->Dragon phase
        1.13,1.12,1.11,1.10
        12 ->Dragon phase
        1.9
        11 ->Dragon phase

         */
        registerMeta(at(11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11), "dragonPhase", 10)
    }

    fun setDragonPhase(dragonPhase: Int) {
        setMetadata("dragonPhase", dragonPhase)
    }

    fun getDragonPhase(): Int {
        return getMetadata("dragonPhase")
    }
}