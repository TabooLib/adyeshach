package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.entity.Mob

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
open class AdyRaider(entityTypes: EntityTypes) : AdyMob(entityTypes) {

    init {
        /*
        1.16,1.15
        15
        1.14
        14
        1.13 -
         */
        registerMeta(at(11500 to 15, 11400 to 14), "isCelebrating", false)
    }
}