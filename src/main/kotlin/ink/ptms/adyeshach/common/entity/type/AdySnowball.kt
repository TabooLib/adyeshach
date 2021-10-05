package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityThrowable
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdySnowball : AdyEntity(EntityTypes.SNOWBALL), EntityThrowable {

    init {
//        registerMeta(at(11700 to 8, 11600 to 7), "item", ItemStack(Material.SNOWBALL))
    }
}