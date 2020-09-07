package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityThrowable
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyThrownExperienceBottle() : AdyEntity(EntityTypes.THROWN_EXPERIENCE_BOTTLE), EntityThrowable {

    init {
        registerMeta(at(11600 to 7), "item", ItemStack(Material.EXPERIENCE_BOTTLE))
    }

    fun getItem(): ItemStack {
        return getMetadata("item")
    }

    fun setItem(value: ItemStack) {
        setMetadata("item", value)
    }
}