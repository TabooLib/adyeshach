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
         * 1.15 -> 15
         * 1.14 -> 14
         * 1.10 -> 12
         * 1.9 -> 11
         */
        registerMeta(at(11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11), "isClimbing", false)
    }

    fun setClimbing(climbing: Boolean) {
        setMetadata("isClimbing", climbing)
    }

    fun isClibmbing(): Boolean {
        return getMetadata("isClimbing")
    }
}