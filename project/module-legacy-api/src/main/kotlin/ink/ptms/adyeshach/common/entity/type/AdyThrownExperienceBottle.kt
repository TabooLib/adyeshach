package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityThrowable
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.inventory.ItemStack

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
class AdyThrownExperienceBottle(v2: ink.ptms.adyeshach.core.entity.EntityInstance):  AdyEntity(EntityTypes.THROWN_EXPERIENCE_BOTTLE, v2), EntityThrowable {

    fun getItem(): ItemStack {
        return getMetadata("item")
    }

    fun setItem(value: ItemStack) {
        setMetadata("item", value)
    }
}