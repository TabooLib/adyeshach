package ink.ptms.adyeshach.impl.nms.parser

import com.eatthepath.uuid.FastUUID
import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import java.util.UUID

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.parser.UUIDParser
 *
 * @author 坏黑
 * @since 2022/6/28 18:30
 */
class UUIDParser : MinecraftMetadataParser<UUID>() {

    override fun parse(value: Any): UUID {
        return if (value is UUID) value else FastUUID.parseUUID(value.toString())
    }

    override fun createMeta(index: Int, value: UUID): MinecraftMeta {
        return metadataHandler().createUUIDMeta(index, value)
    }
}