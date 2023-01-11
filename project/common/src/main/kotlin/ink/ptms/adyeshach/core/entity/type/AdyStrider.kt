package ink.ptms.adyeshach.core.entity.type

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyStrider : AdyMob {

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