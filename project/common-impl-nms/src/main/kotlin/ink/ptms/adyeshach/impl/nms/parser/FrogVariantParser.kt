package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import ink.ptms.adyeshach.core.bukkit.BukkitFrogVariant
import ink.ptms.adyeshach.core.util.getEnumOrNull

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.parser.FrogVariantParser
 *
 * @author 坏黑
 * @since 2022/6/28 23:25
 */
class FrogVariantParser : MinecraftMetadataParser<BukkitFrogVariant>() {

    override fun parse(value: Any): BukkitFrogVariant {
        return if (value is BukkitFrogVariant) value else BukkitFrogVariant::class.java.getEnumOrNull(value) ?: BukkitFrogVariant.TEMPERATE
    }

    override fun createMeta(index: Int, value: BukkitFrogVariant): MinecraftMeta {
        return metadataHandler().createFrogVariantMeta(index, value)
    }
}