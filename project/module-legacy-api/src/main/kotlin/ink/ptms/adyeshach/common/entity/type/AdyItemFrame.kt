package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.inventory.ItemStack

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
open class AdyItemFrame(entityTypes: EntityTypes, v2: ink.ptms.adyeshach.core.entity.EntityInstance) : AdyEntity(entityTypes, v2) {

    constructor(v2: ink.ptms.adyeshach.core.entity.EntityInstance): this(EntityTypes.ITEM_FRAME, v2)

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