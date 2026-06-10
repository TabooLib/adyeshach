package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import ink.ptms.adyeshach.core.bukkit.BukkitChickenType
import ink.ptms.adyeshach.core.util.getEnumOrNull

class ChickenVariantParser : MinecraftMetadataParser<BukkitChickenType>() {
    override fun parse(value: Any): BukkitChickenType {
        return if (value is BukkitChickenType) value else BukkitChickenType::class.java.getEnumOrNull(value) ?: BukkitChickenType.TEMPERATE
    }

    override fun createMeta(index: Int, value: BukkitChickenType): MinecraftMeta {
        return metadataHandler().createChickenVariantMeta(index, value)
    }
}