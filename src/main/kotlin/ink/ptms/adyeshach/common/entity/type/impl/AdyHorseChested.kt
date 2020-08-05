package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.api.nms.NMS
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-04 18:28
 */
abstract class AdyHorseChested(owner: Player, entityTypes: EntityTypes) : AdyHorseBase(owner, entityTypes) {

    init {
        registerMeta(18, "hasChest", false)
    }

    fun setHasChest(value: Boolean) {
        setMetadata("hasChest", value)
    }

    fun isHasChest(): Boolean {
        return getMetadata("hasChest")
    }
}