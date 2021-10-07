package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyPillager : AdyRaider(EntityTypes.PILLAGER) {

    init {
//        natural(at(11700 to 17), "isCharging", false)
    }

    var isCharging: Boolean
        get() = getMetadata("isCharging")
        set(value) {
            setMetadata("isCharging", value)
        }
}