package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyFireworkRocket : AdyEntity(EntityTypes.FIREWORK_ROCKET) {

    init {
        /*
        1.16,1.15,1.14
        7 ->Firework info
        8 ->Entity ID of entity which used firework(for elytra boosting)
        9 ->Shot at angle(from a crossbow)
        1.13,1.12,1.11
        6 ->Firework info
        7 ->Entity ID of entity which used firework(for elytra boosting)
        1.10
        6 ->Firework info
        1.9
        5 ->Firework info
         */
        registerMeta(at(11700 to 8, 11400 to 7, 11100 to 6, 10900 to 9), "fireworkInfo", ItemStack(Material.AIR))
    }

    fun getFireworkInfo(): ItemStack {
        return getMetadata("fireworkInfo")
    }

    fun setFireworkInfo(fireworkInfo: ItemStack) {
        setMetadata("fireworkInfo", fireworkInfo)
    }
}