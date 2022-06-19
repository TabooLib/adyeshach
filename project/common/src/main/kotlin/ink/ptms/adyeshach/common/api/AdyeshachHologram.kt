package ink.ptms.adyeshach.common.api

import ink.ptms.adyeshach.common.entity.type.AdyEntity
import org.bukkit.Location
import org.bukkit.inventory.ItemStack

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.api.AdyeshachHologram
 *
 * @author 坏黑
 * @since 2022/6/16 18:12
 */
interface AdyeshachHologram {

    /**
     * 传送到某处
     */
    fun teleport(location: Location)

    /**
     * 更新内容
     */
    fun update(content: List<Item>)

    /**
     * 删除全息
     */
    fun delete()

    /**
     * 全息条目
     */
    interface Item {

        /**
         * 间距
         */
        var space: Double
    }

    interface ItemByItemStack {

        /**
         * 显示物品
         */
        var itemStack: ItemStack
    }

    interface ItemByText {

        /**
         * 显示文本
         */
        var text: String
    }

    interface ItemByEntity<T : AdyEntity> {

        /**
         * 显示实体
         */
        val entity: T
    }
}