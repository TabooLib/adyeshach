package ink.ptms.adyeshach.core.entity.type

/**
 * @author sky
 * @date 2020/8/4 22:18
 */
interface AdyInteraction : AdyEntity {

    fun setWidth(width: Float) {
        setMetadata("width", width)
    }

    fun getWidth(): Float {
        return getMetadata("width")
    }

    fun setHeight(height: Float) {
        setMetadata("height", height)
    }

    fun getHeight(): Float {
        return getMetadata("height")
    }

    fun setResponsive(responsive: Boolean) {
        setMetadata("responsive", responsive)
    }

    fun isResponsive(): Boolean {
        return getMetadata("responsive")
    }
}