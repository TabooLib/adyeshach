package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
class AdyPhantom(v2: ink.ptms.adyeshach.core.entity.EntityInstance):  AdyMob(EntityTypes.PHANTOM, v2) {

    fun setSize(size: Int) {
        setMetadata("size", size)
    }

    fun getSize(): Int {
        return getMetadata("size")
    }
}