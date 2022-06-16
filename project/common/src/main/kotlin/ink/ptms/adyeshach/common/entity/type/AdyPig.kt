package ink.ptms.adyeshach.common.entity.type

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
abstract class AdyPig : AdyEntityAgeable() {

    open fun setSaddle(value: Boolean) {
        setMetadata("hasSaddle", value)
    }

    open fun hasSaddle(): Boolean {
        return getMetadata("hasSaddle")
    }
}