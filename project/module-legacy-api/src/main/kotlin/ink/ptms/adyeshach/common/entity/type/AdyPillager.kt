package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
class AdyPillager(v2: ink.ptms.adyeshach.core.entity.EntityInstance):  AdyRaider(EntityTypes.PILLAGER, v2) {

    var isCharging: Boolean
        get() = getMetadata("isCharging")
        set(value) {
            setMetadata("isCharging", value)
        }
}