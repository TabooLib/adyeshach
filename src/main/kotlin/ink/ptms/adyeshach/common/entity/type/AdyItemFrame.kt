package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyItemFrame() : AdyEntity(EntityTypes.ITEM_FRAME) {

    init {
        /**
         * 1.13 -> 7
         * 1.10 -> 6
         * 1.9 -> 5
         */
        val index = at(11300 to 7, 11000 to 6, 10900 to 5)

        registerMeta(index, "item", ItemStack(Material.AIR))
        registerMeta(index + 1, "rotation", 0)
    }

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