package ink.ptms.adyeshach.core.entity.type

import ink.ptms.adyeshach.core.bukkit.BukkitZombieNautilusVariant

/**
 * 僵尸鹦鹉螺（1.21.11+）
 */
interface AdyZombieNautilus : AdyNautilus {

    fun setVariant(value: BukkitZombieNautilusVariant) {
        setMetadata("zombieNautilusVariant", value)
    }

    fun getVariant(): BukkitZombieNautilusVariant {
        return getMetadata("zombieNautilusVariant")
    }
}
