package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitMushroom

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
abstract class AdyMushroom : AdyCow() {

    open fun setType(value: BukkitMushroom) {
        setMetadata("type", value.name.lowercase())
    }

    open fun getType(): BukkitMushroom {
        return BukkitMushroom.valueOf(getMetadata<String>("type").uppercase())
    }
}