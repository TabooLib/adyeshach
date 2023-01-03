package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityFireball
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.inventory.ItemStack

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
class AdyFireball(v2: ink.ptms.adyeshach.core.entity.EntityInstance):  AdyEntity(EntityTypes.FIREBALL, v2), EntityFireball {

    fun getItem(): ItemStack {
        return getMetadata("item")
    }

    fun setItem(value: ItemStack) {
        setMetadata("item", value)
    }
}