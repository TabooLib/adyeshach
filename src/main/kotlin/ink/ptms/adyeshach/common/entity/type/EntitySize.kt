package ink.ptms.adyeshach.common.entity.type

/**
 * @Author sky
 * @Since 2020-08-04 13:27
 */
class EntitySize(val width: Double, val height: Double, val fix: Boolean) {

    constructor(width: Double, height: Double) : this(width, height, false)

}