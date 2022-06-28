package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.common.api.MinecraftMeta
import ink.ptms.adyeshach.common.api.MinecraftMetadataParser
import net.md_5.bungee.api.chat.TextComponent

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.parser.TextComponentParser
 *
 * @author 坏黑
 * @since 2022/6/28 18:38
 */
class TextComponentParser : MinecraftMetadataParser<String>() {

    override fun parse(value: Any): String {
        return if (value is TextComponent) value.text else value.toString()
    }

    override fun createMeta(index: Int, value: String): MinecraftMeta {
        return metadataHandler().createOptionalComponentMeta(index, value.ifEmpty { null })
    }
}