package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author Arasple
 * @date 2020/8/5 22:35
 */
open class AdySpider(entityTypes: EntityTypes) : AdyMob(entityTypes) {

    constructor(): this(EntityTypes.SPIDER)

    init {
        /**
         * 1.15 -> 15
         * 1.14 -> 14
         * 1.10 -> 12
         * 1.9 -> 11
         */
//        mask(at(11700 to 16, 11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11), "isClimbing", 0x01)
    }

    fun setClimbing(climbing: Boolean) {
        setMetadata("isClimbing", climbing)
    }

    fun isClimbing(): Boolean {
        return getMetadata("isClimbing")
    }

}