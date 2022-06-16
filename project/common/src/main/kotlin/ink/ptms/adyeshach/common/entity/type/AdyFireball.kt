package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityFireball
import org.bukkit.inventory.ItemStack

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
abstract class AdyFireball : AdyEntity(), EntityFireball {

    open fun getItem(): ItemStack {
        return getMetadata("item")
    }

    open fun setItem(value: ItemStack) {
        setMetadata("item", value)
    }
}