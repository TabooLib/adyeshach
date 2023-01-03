package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
open class AdySlime(entityTypes: EntityTypes, v2: ink.ptms.adyeshach.core.entity.EntityInstance) : AdyMob(entityTypes, v2) {

    constructor(v2: ink.ptms.adyeshach.core.entity.EntityInstance) : this(EntityTypes.SLIME, v2)

    fun getSize(): Int {
        return getMetadata("size")
    }

    fun setSize(size: Int) {
        setMetadata("size", size)
    }
}