package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import ink.ptms.adyeshach.core.bukkit.BukkitCatType
import ink.ptms.adyeshach.core.util.getEnumOrNull

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.parser.CatVariantParser
 *
 * @author 坏黑
 * @since 2022/6/28 23:25
 */
class CatVariantParser : MinecraftMetadataParser<BukkitCatType>() {

    override fun parse(value: Any): BukkitCatType {
        return if (value is BukkitCatType) value else BukkitCatType::class.java.getEnumOrNull(value) ?: BukkitCatType.TABBY
    }

    override fun createMeta(index: Int, value: BukkitCatType): MinecraftMeta {
        return metadataHandler().createCatVariantMeta(index, value)
    }
}