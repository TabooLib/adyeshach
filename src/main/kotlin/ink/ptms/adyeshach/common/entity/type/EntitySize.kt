package ink.ptms.adyeshach.common.entity.type

/**
 * @Author sky
 * @Since 2020-08-04 13:27
 */
class EntitySize(val height: Double, val weight: Double, val fix: Boolean) {

    constructor(height: Double, weight: Double): this(height, weight, false)
}