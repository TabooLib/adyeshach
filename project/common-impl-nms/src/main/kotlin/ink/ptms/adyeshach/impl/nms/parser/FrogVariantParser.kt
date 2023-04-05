package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import ink.ptms.adyeshach.core.util.getEnumOrNull
import org.bukkit.entity.Frog
import taboolib.common.platform.function.info

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.parser.FrogVariantParser
 *
 * @author 坏黑
 * @since 2022/6/28 23:25
 */
class FrogVariantParser : MinecraftMetadataParser<Frog.Variant>() {

    override fun parse(value: Any): Frog.Variant {
        return if (value is Frog.Variant) value else Frog.Variant::class.java.getEnumOrNull(value) ?: Frog.Variant.TEMPERATE
    }

    override fun createMeta(index: Int, value: Frog.Variant): MinecraftMeta {
        return metadataHandler().createFrogVariantMeta(index, value)
    }
}