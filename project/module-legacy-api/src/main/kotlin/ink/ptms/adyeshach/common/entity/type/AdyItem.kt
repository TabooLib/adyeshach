package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.inventory.ItemStack

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
class AdyItem(v2: ink.ptms.adyeshach.core.entity.EntityInstance):  AdyEntity(EntityTypes.ITEM, v2) {

    fun setItem(itemStack: ItemStack) {
        setMetadata("item", itemStack)
        respawn()
    }

    fun getItem(): ItemStack {
        return getMetadata("item")
    }
}