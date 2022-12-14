package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.parser.StringParser
 *
 * @author 坏黑
 * @since 2022/6/28 18:30
 */
class StringParser : MinecraftMetadataParser<String>() {

    override fun parse(value: Any): String {
        return value.toString()
    }

    override fun createMeta(index: Int, value: String): MinecraftMeta {
        return metadataHandler().createStringMeta(index, value)
    }
}