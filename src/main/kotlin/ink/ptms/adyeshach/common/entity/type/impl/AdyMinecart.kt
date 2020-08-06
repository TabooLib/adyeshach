package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.type.EntityTypes
import org.bukkit.entity.Player

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
open class AdyMinecart(owner: Player, entityTypes: EntityTypes) : EntityInstance(owner, entityTypes) {

    constructor(owner: Player): this(owner, EntityTypes.MINECART)
}