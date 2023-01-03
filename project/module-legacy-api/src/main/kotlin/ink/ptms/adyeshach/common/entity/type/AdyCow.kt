package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
open class AdyCow(entityTypes: EntityTypes, v2: ink.ptms.adyeshach.core.entity.EntityInstance) : AdyEntityAgeable(entityTypes, v2) {

    constructor(v2: ink.ptms.adyeshach.core.entity.EntityInstance): this(EntityTypes.COW, v2)
}