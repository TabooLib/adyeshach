package ink.ptms.adyeshach.core.entity.type

import ink.ptms.adyeshach.core.entity.EntityThrowable
import org.bukkit.inventory.ItemStack

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyThrownPotion : AdyEntity, EntityThrowable {

    fun getItem(): ItemStack {
        return getMetadata("item")
    }

    fun setItem(item: ItemStack) {
        setMetadata("item", item)
    }
}