package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
class AdyPufferfish(v2: ink.ptms.adyeshach.core.entity.EntityInstance):  AdyFish(EntityTypes.PUFFERFISH, v2) {

    fun getPuffState(): Int {
        return getMetadata("puffState")
    }

    fun setPuffState(value: Int) {
        setMetadata("puffState", value)
    }
}