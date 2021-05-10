package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyPufferfish : AdyFish(EntityTypes.PUFFERFISH) {

    init {
        registerMeta(at(11500 to 16, 11400 to 15, 11300 to 13), "puffState", 0)
    }

    fun getPuffState(): Int {
        return getMetadata("puffState")
    }

    fun setPuffState(value: Int) {
        setMetadata("puffState", value)
    }
}