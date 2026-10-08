package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import ink.ptms.adyeshach.core.bukkit.BukkitZombieNautilusVariant
import ink.ptms.adyeshach.core.util.getEnumOrNull

class ZombieNautilusVariantParser : MinecraftMetadataParser<BukkitZombieNautilusVariant>() {

    override fun parse(value: Any): BukkitZombieNautilusVariant {
        return value as? BukkitZombieNautilusVariant ?: BukkitZombieNautilusVariant::class.java.getEnumOrNull(value) ?: BukkitZombieNautilusVariant.TEMPERATE
    }

    override fun createMeta(index: Int, value: BukkitZombieNautilusVariant): MinecraftMeta {
        return metadataHandler().createZombieNautilusVariantMeta(index, value)
    }
}
