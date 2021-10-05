package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyStrider : AdyEntityLiving(EntityTypes.STRIDER) {

    init {
//        registerMeta(at(11700 to 18), "isShaking", false)
//        registerMeta(at(11700 to 19), "hasSaddle", false)
    }

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