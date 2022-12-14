package ink.ptms.adyeshach.core.entity.type

import org.bukkit.inventory.ItemStack

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyItemFrame : AdyEntity {

    fun getItem(): ItemStack {
        return getMetadata("item")
    }

    fun setItem(itemStack: ItemStack) {
        setMetadata("item", itemStack)
    }

    fun getRotation(): Int {
        return getMetadata("rotation")
    }

    fun setRotation(rotation: Int) {
        setMetadata("rotation", rotation)
    }
}