package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.inventory.ItemStack

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
class AdyFireworkRocket(v2: ink.ptms.adyeshach.core.entity.EntityInstance):  AdyEntity(EntityTypes.FIREWORK_ROCKET, v2) {

    init {
        testing = true
    }

    fun getFireworkInfo(): ItemStack {
        return getMetadata("fireworkInfo")
    }

    fun setFireworkInfo(fireworkInfo: ItemStack) {
        setMetadata("fireworkInfo", fireworkInfo)
    }
}