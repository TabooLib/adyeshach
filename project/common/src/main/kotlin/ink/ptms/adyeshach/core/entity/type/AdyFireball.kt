package ink.ptms.adyeshach.core.entity.type

import ink.ptms.adyeshach.core.entity.EntityFireball
import org.bukkit.inventory.ItemStack

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyFireball : AdyEntity, EntityFireball {

    fun getItem(): ItemStack {
        return getMetadata("item")
    }

    fun setItem(value: ItemStack) {
        setMetadata("item", value)
    }
}