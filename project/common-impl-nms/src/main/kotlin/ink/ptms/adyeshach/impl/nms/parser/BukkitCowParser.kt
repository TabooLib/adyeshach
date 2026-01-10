package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import ink.ptms.adyeshach.core.bukkit.BukkitCowVariant
import ink.ptms.adyeshach.core.util.getEnumOrNull

class BukkitCowParser : MinecraftMetadataParser<BukkitCowVariant>() {
    override fun parse(value: Any): BukkitCowVariant {
        return value as? BukkitCowVariant ?: BukkitCowVariant::class.java.getEnumOrNull(value) ?: BukkitCowVariant.NORMAL
    }

    override fun createMeta(index: Int, value: BukkitCowVariant): MinecraftMeta {
        return metadataHandler().createCowVariantMeta(index, value)
    }
}