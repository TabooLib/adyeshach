package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import ink.ptms.adyeshach.core.util.Components

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.parser.OptTextComponentParser
 *
 * @author 坏黑
 * @since 2022/6/28 18:38
 */
class OptChatParser : MinecraftMetadataParser<String>() {

    override fun parse(value: Any): String {
        return Components.toRawMessage(value)
    }

    override fun createMeta(index: Int, value: String): MinecraftMeta {
         return metadataHandler().createOptChatMeta(index, value.ifEmpty { null })
    }
}