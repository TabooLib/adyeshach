package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import ink.ptms.adyeshach.core.util.getEnumOrNull
import org.bukkit.Art

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.parser.PaintingVariantParser
 *
 * @author 坏黑
 * @since 2022/6/28 23:25
 */
class PaintingVariantParser : MinecraftMetadataParser<Art>() {

    override fun parse(value: Any): Art {
        return if (value is Art) value else Art::class.java.getEnumOrNull(value) ?: Art.KEBAB
    }

    override fun createMeta(index: Int, value: Art): MinecraftMeta {
        return metadataHandler().createPaintingVariantMeta(index, value)
    }
}