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
        /**
         * 1.15 -> 18
         * 1.14 -> 17
         * 1.11 -> 15
         */
        registerMeta(at(11500 to 18, 11400 to 17, 11100 to 15), "hasChest", false)
    }

    override fun setHasChest(value: Boolean) {
        setMetadata("hasChest", value)
    }

    override fun isHasChest(): Boolean {
        return getMetadata("hasChest")
    }
}