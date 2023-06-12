package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import ink.ptms.adyeshach.core.entity.type.AdySniffer
import ink.ptms.adyeshach.core.util.getEnumOrNull
import org.bukkit.entity.Cat

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.parser.SnifferStateParser
 *
 * @author 坏黑
 * @since 2022/6/28 23:25
 */
class SnifferStateParser : MinecraftMetadataParser<AdySniffer.State>() {

    override fun parse(value: Any): AdySniffer.State {
        return if (value is AdySniffer.State) value else AdySniffer.State::class.java.getEnumOrNull(value) ?: AdySniffer.State.IDLING
    }

    override fun createMeta(index: Int, value: AdySniffer.State): MinecraftMeta {
        return metadataHandler().createSnifferStateMeta(index, value)
    }
}