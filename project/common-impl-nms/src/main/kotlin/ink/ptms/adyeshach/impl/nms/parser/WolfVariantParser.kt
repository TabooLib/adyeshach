package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import ink.ptms.adyeshach.core.bukkit.BukkitWolfVariant
import ink.ptms.adyeshach.core.util.getEnumOrNull

class WolfVariantParser : MinecraftMetadataParser<BukkitWolfVariant>() {
    override fun parse(value: Any): BukkitWolfVariant {
        return if (value is BukkitWolfVariant) value else BukkitWolfVariant::class.java.getEnumOrNull(value) ?: BukkitWolfVariant.PALE
    }

    override fun createMeta(index: Int, value: BukkitWolfVariant): MinecraftMeta {
        return metadataHandler().createWolfVariantMeta(index, value)
    }

}