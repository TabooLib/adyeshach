package ink.ptms.adyeshach.impl.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.type.AdyItem
import org.bukkit.inventory.ItemStack

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.type.DefaultItem
 *
 * @author 坏黑
 * @since 2022/6/29 19:05
 */
abstract class DefaultItem(entityTypes: EntityTypes) : DefaultEntity(entityTypes), AdyItem {

    override fun setItem(itemStack: ItemStack) {
        setMetadata("item", itemStack)
        respawn()
    }

    override fun getItem(): ItemStack {
        return getMetadata("item")
    }
}