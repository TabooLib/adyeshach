package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import taboolib.common5.cfloat

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.parser.FloatParser
 *
 * @author 坏黑
 * @since 2022/6/28 18:29
 */
class FloatParser : MinecraftMetadataParser<Float>() {

    override fun parse(value: Any): Float {
        return value.cfloat
    }

    override fun createMeta(index: Int, value: Float): MinecraftMeta {
        return metadataHandler().createFloatMeta(index, value)
    }
}