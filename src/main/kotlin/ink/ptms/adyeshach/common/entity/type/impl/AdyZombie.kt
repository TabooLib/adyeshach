package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.api.nms.NMS
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-04 18:28
 */
open class AdyZombie(owner: Player, entityTypes: EntityTypes) : AdyEntityAgeable(owner, entityTypes) {

    init {
        registerMeta(17, "isDrowning", false)
    }

    fun setDrowning(value: Boolean) {
        setMetadata("isDrowning", value)
    }

    fun isDrowning(): Boolean {
        return getMetadata("isDrowning")
    }
}