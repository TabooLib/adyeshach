package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.parser.IntParser
 *
 * @author 坏黑
 * @since 2022/6/28 18:27
 */
class IntParser : MinecraftMetadataParser<Int>() {

    override fun parse(value: Any): Int {
        return value.toInt()
    }

    override fun createMeta(index: Int, value: Int): MinecraftMeta {
        return metadataHandler().createIntMeta(index, value)
    }
}