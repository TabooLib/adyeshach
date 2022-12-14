package ink.ptms.adyeshach.core.entity.type

import org.bukkit.inventory.ItemStack

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyFireworkRocket : AdyEntity {

    fun getFireworkInfo(): ItemStack {
        return getMetadata("fireworkInfo")
    }

    fun setFireworkInfo(fireworkInfo: ItemStack) {
        setMetadata("fireworkInfo", fireworkInfo)
    }
}