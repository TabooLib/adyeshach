package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.inventory.ItemStack

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyItem : AdyEntity(EntityTypes.ITEM) {

    init {
        /**
         * 1.13 -> 7
         * 1.10 -> 6
         * 1.9 -> 5
         */
//        natural(at(11700 to 8, 11300 to 7, 11000 to 6, 10900 to 5), "item", ItemStack(Material.BEDROCK))
    }

    fun setItem(itemStack: ItemStack) {
        setMetadata("item", itemStack)
    }

    fun getItem(): ItemStack {
        return getMetadata("item")
    }
}