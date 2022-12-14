package ink.ptms.adyeshach.core.entity

/**
 * @author sky
 * @since 2020-08-04 13:27
 */
data class EntitySize(val width: Double, val height: Double, val fix: Boolean) {

    constructor(width: Double, height: Double) : this(width, height, false)
}