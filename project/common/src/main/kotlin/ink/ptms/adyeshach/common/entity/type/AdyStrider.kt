package ink.ptms.adyeshach.common.entity.type

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyStrider : AdyEntityLiving {

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