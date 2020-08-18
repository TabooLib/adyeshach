package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyEyeOfEnder() : AdyEntity(EntityTypes.EYE_OF_ENDER) {

    init {
        /*
        1.16,1.15,1.14
        7 ->Item
         */
        registerMeta(at(11400 to 7), "item", ItemStack(Material.ENDER_EYE))
    }
}