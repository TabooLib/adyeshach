package ink.ptms.adyeshach.common.entity.type

import org.bukkit.inventory.ItemStack

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyItem : AdyEntity {

    /**
     * 设置物品
     */
    fun setItem(itemStack: ItemStack)

    /**
     * 获取物品
     */
    fun getItem(): ItemStack
}