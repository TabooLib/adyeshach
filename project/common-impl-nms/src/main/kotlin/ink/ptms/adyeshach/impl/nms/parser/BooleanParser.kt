package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.common.api.MinecraftMeta
import ink.ptms.adyeshach.common.api.MinecraftMetadataParser
import taboolib.common5.Coerce

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.parser.BooleanParser
 *
 * @author 坏黑
 * @since 2022/6/28 18:32
 */
class BooleanParser : MinecraftMetadataParser<Boolean>() {

    override fun parse(value: Any): Boolean {
        return Coerce.toBoolean(value)
    }

    override fun createMeta(index: Int, value: Boolean): MinecraftMeta {
        return metadataHandler().createBooleanMeta(index, value)
    }
}