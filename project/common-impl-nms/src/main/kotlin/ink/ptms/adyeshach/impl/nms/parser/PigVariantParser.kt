package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import ink.ptms.adyeshach.core.bukkit.BukkitPigVariant
import ink.ptms.adyeshach.core.util.getEnumOrNull

class PigVariantParser : MinecraftMetadataParser<BukkitPigVariant>() {
    override fun parse(value: Any): BukkitPigVariant {
        return if (value is BukkitPigVariant) value else BukkitPigVariant::class.java.getEnumOrNull(value) ?: BukkitPigVariant.TEMPERATE
    }

    override fun createMeta(index: Int, value: BukkitPigVariant): MinecraftMeta {
        return metadataHandler().createPigVariantMeta(index, value)
    }
}