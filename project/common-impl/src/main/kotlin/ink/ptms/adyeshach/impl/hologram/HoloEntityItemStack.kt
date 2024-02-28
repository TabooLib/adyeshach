package ink.ptms.adyeshach.impl.hologram

import ink.ptms.adyeshach.core.AdyeshachHologram
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.AdyItem
import org.bukkit.inventory.ItemStack

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.hologram.HoloEntityItemStack
 *
 * @author 坏黑
 * @since 2022/12/14 13:38
 */
class HoloEntityItemStack(itemStack: ItemStack, space: Double) : HoloEntity<AdyItem>(space), AdyeshachHologram.ItemByItemStack {

    override val type = EntityTypes.ITEM
    override var itemStack = itemStack
        set(value) {
            if (field != value) {
                field = value
                entity?.setItem(itemStack)
            }
        }

    override fun prepareSpawn(entity: AdyItem) {
        entity.setItem(itemStack)
        entity.setNoGravity(true)
        entity.setVelocity(0.0, 0.0, 0.0)
    }

    override fun merge(item: Any): Boolean {
        if (item is HoloEntityItemStack) {
            itemStack = item.itemStack
            return true
        }
        if (item is ItemStack) {
            itemStack = item
            return true
        }
        return false
    }
}