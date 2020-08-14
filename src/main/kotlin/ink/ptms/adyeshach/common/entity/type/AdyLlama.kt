package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
open class AdyLlama(entityTypes: EntityTypes) : AdyEntityLiving(entityTypes) {

    constructor(): this(EntityTypes.LLAMA)
}