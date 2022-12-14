package ink.ptms.adyeshach.core.entity.type

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyPig : AdyEntityAgeable {

    fun setSaddle(value: Boolean) {
        setMetadata("hasSaddle", value)
    }

    fun hasSaddle(): Boolean {
        return getMetadata("hasSaddle")
    }
}