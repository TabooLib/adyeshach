package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyPhantom() : AdyMob(EntityTypes.PHANTOM) {

    init {
        registerMeta(at(11500 to 15, 11400 to 14, 11300 to 12), "size", 0)
    }

    fun setSize(size: Int) {
        setMetadata("size", size)
    }

    fun getSize(): Int {
        return getMetadata("size")
    }
}