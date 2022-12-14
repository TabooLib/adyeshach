package ink.ptms.adyeshach.core.entity.type

import ink.ptms.adyeshach.core.bukkit.BukkitMushroom

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyMushroom : AdyCow {

    fun setType(value: BukkitMushroom) {
        setMetadata("type", value.name.lowercase())
    }

    fun getType(): BukkitMushroom {
        return BukkitMushroom.valueOf(getMetadata<String>("type").uppercase())
    }
}