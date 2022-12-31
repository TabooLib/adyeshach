package ink.ptms.adyeshach.core

import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.manager.Manager
import ink.ptms.adyeshach.core.entity.type.AdyEntity
import org.bukkit.Location
import org.bukkit.inventory.ItemStack

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.AdyeshachHologram
 *
 * @author 坏黑
 * @since 2022/6/16 18:12
 */
interface AdyeshachHologram {

    /** 移动全息 */
    fun teleport(location: Location)

    /** 更新条目 */
    fun update(content: List<Any>)

    /**
     * 安全更新条目
     * 如果行数、类型相同则更新内容，如果行数不同则重新生成
     */
    fun updateSafely(content: List<Any>) {
        val current = contents()
        if (current.size == content.size && current.map { it.javaClass } == content.map { it.javaClass }) {
            current.forEachIndexed { index, item -> item.merge(content[index]) }
        } else {
            update(content)
        }
    }

    /** 删除全息 */
    fun remove()

    /** 获取所有条目 */
    fun contents(): List<Item>

    /** 全息条目 */
    interface Item {

        /** 间距 */
        var space: Double

        /** 删除全息 */
        fun remove()

        /**
         * 合并相同类型的全息条目
         * 如果类型不同则返回否
         */
        fun merge(item: Any): Boolean
    }

    /** 文本类型全息条目 */
    interface ItemByText : Item {

        /** 显示文本 */
        var text: String
    }

    /** 物品类型全息条目 */
    interface ItemByItemStack : Item {

        /** 显示物品 */
        var itemStack: ItemStack
    }

    /** 实体类型全息条目 */
    interface ItemByEntity<T : AdyEntity> : Item {

        /** 实体类型 */
        val type: EntityTypes

        /** 实体对象 */
        fun entity(): T

        /** 生成全息 */
        fun spawn(offset: Double, location: Location, manager: Manager)

        /** 移动全息 */
        fun teleport(location: Location)
    }
}