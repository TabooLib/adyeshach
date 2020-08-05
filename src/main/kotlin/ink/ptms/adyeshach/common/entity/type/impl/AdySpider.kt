package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.type.EntityTypes
import org.bukkit.entity.Player

/**
 * @author Arasple
 * @date 2020/8/5 22:35
 */
abstract class AdySpider(owner: Player, entityTypes: EntityTypes) : AdyEntityLiving(owner, entityTypes) {

    init {
        /**
         * 1.13,1.12 -> Index 12
         * 1.9 -> Index 11
         */
        registerMeta(15, "isClimbing", false)
    }

    fun setClimbing(climbing: Boolean) {
        setMetadata("isClimbing", climbing)
    }

    fun isClibmbing(): Boolean {
        return getMetadata("isClimbing")
    }

}