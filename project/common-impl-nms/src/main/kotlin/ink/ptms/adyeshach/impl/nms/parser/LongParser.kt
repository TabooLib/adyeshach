package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import taboolib.common5.clong

class LongParser : MinecraftMetadataParser<Long>() {

    override fun parse(value: Any): Long {
        return value.clong
    }

    override fun createMeta(index: Int, value: Long): MinecraftMeta {
        return metadataHandler().createLongMeta(index, value)
    }
}
