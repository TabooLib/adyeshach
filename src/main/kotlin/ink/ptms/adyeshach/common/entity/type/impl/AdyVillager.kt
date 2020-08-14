package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.type.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
open class AdyVillager(entityTypes: EntityTypes) : AdyEntityAgeable(entityTypes) {

    constructor() : this(EntityTypes.VILLAGER)

}