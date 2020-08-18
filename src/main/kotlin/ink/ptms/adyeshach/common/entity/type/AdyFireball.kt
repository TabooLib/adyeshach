package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityFireball
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyFireball() : AdyEntity(EntityTypes.FIREBALL), EntityFireball {

    init {
        /*
        1.16
        7 ->Item
        1.15,1.14,1.13,1.12,1.11,1.10,1.9
        null
         */
        registerMeta(at(11600 to 7), "item", ItemStack(Material.AIR))
    }
}