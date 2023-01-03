package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
class AdyStrider(v2: ink.ptms.adyeshach.core.entity.EntityInstance):  AdyEntityLiving(EntityTypes.STRIDER, v2) {

    var isShaking: Boolean
        get() = getMetadata("isShaking")
        set(value) {
            setMetadata("isShaking", value)
        }

    var hasSaddle: Boolean
        get() = getMetadata("hasSaddle")
        set(value) {
            setMetadata("hasSaddle", value)
        }
}