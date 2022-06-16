package ink.ptms.adyeshach.common.entity.type

import org.bukkit.inventory.ItemStack

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
abstract class AdyItemFrame : AdyEntity() {

    open fun getItem(): ItemStack {
        return getMetadata("item")
    }

    open fun setItem(itemStack: ItemStack) {
        setMetadata("item", itemStack)
    }

    open fun getRotation(): Int {
        return getMetadata("rotation")
    }

    open fun setRotation(rotation: Int) {
        setMetadata("rotation", rotation)
    }
}