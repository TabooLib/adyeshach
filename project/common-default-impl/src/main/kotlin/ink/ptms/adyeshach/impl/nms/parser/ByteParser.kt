package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.common.api.MinecraftMeta
import ink.ptms.adyeshach.common.api.MinecraftMetadataParser

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.parser.ByteParser
 *
 * @author 坏黑
 * @since 2022/6/28 18:29
 */
class ByteParser : MinecraftMetadataParser<Byte>() {

    override fun parse(value: Any): Byte {
        return value.toByte()
    }

    override fun createMeta(index: Int, value: Byte): MinecraftMeta {
        return metadataHandler().createByteMeta(index, value)
    }
}