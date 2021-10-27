package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityFireball
import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdySmallFireball : AdyEntity(EntityTypes.SMALL_FIREBALL), EntityFireball {

    init {
//        natural(at(11700 to 8, 11600 to 7), "item", ItemStack(Material.FIRE_CHARGE))
    }
}