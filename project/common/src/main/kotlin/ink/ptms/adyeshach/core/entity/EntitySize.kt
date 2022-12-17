package ink.ptms.adyeshach.core.entity

/**
 * @author sky
 * @since 2020-08-04 13:27
 */
data class EntitySize(val height: Double, val width: Double, val fix: Boolean) {

    constructor(height: Double, width: Double) : this(height, width, false)
}