package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
open class AdySlime(entityTypes: EntityTypes) : AdyMob(entityTypes) {

    constructor() : this(EntityTypes.SLIME)

    init {
//        registerMeta(at(11700 to 16, 11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11), "size", 1)
    }

    fun getSize(): Int {
        return getMetadata("size")
    }

    fun setSize(size: Int) {
        setMetadata("size", size)
    }
}