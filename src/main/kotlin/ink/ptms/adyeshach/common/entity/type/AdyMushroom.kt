package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitMushroom
import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyMushroom : AdyCow(EntityTypes.MUSHROOM) {

    fun setType(value: BukkitMushroom) {
        setMetadata("type", value.name.lowercase())
    }

    fun getType(): BukkitMushroom {
        return BukkitMushroom.valueOf(getMetadata<String>("type").uppercase())
    }
}