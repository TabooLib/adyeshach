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
    fun update(content: List<Item>)

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