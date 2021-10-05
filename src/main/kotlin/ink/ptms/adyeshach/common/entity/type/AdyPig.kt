package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyPig : AdyEntityAgeable(EntityTypes.PIG) {

    init {
        /*
        1.16,1.15
        16 ->Has saddle
        17 ->Total time to "boost" with a carrot on a stick for
        1.14
        15 ->Has saddle
        16 ->Total time to "boost" with a carrot on a stick for
        1.13,1.12,1.11
        13 ->Has saddle
        14 ->Total time to "boost" with a carrot on a stick for
        1.10
        13 ->Has saddle
        1.9
        12 ->Has saddle
         */
//        registerMeta(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "hasSaddle", false)
    }

    fun setSaddle(value: Boolean) {
        setMetadata("hasSaddle", value)
    }

    fun hasSaddle(): Boolean {
        return getMetadata("hasSaddle")
    }
}