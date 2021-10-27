package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityThrowable
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.inventory.ItemStack

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyThrownPotion : AdyEntity(EntityTypes.THROWN_POTION), EntityThrowable {

    init {
//        natural(at(11700 to 8, 11600 to 7), "item", ItemStack(Material.AIR))
    }

    fun getItem(): ItemStack {
        return getMetadata("item")
    }

    fun setItem(item: ItemStack) {
        setMetadata("item", item)
    }
}