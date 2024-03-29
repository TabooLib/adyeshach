package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import taboolib.common5.cbyte

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.parser.ByteParser
 *
 * @author 坏黑
 * @since 2022/6/28 18:29
 */
class ByteParser : MinecraftMetadataParser<Byte>() {

    override fun parse(value: Any): Byte {
        return value.cbyte
    }

    override fun createMeta(index: Int, value: Byte): MinecraftMeta {
        return metadataHandler().createByteMeta(index, value)
    }
}