package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.type.EntityTypes
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-04 18:28
 */
abstract class AdyEntityAgeable(owner: Player, entityTypes: EntityTypes) : AdyEntityLiving(owner, entityTypes) {

    init {
        registerMeta(15, "isBaby", false)
    }

    open fun setBaby(value: Boolean) {
        setMetadata("isBaby", value)
    }

    open fun isBaby(): Boolean {
        return getMetadata("isBaby")
    }
}