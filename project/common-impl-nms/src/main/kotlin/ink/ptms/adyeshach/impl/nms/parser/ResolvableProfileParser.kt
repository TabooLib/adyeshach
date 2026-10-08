package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser

class ResolvableProfileParser : MinecraftMetadataParser<String>() {

    override fun parse(value: Any): String {
        return value.toString()
    }

    override fun createMeta(index: Int, value: String): MinecraftMeta {
        return metadataHandler().createResolvableProfileMeta(index, value)
    }
}
