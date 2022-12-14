package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import org.bukkit.Material
import org.bukkit.material.MaterialData
import taboolib.library.xseries.parseToMaterial

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.parser.BlockStateParser
 *
 * @author 坏黑
 * @since 2022/6/28 19:01
 */
class BlockStateParser : MinecraftMetadataParser<MaterialData>() {

    override fun parse(value: Any): MaterialData {
        return if (value is Map<*, *>) {
            MaterialData(value["type"].toString().replace("LEGACY_", "").parseToMaterial(), value["data"]!!.toByte())
        } else {
            value as MaterialData
        }
    }

    override fun createMeta(index: Int, value: MaterialData): MinecraftMeta {
        return metadataHandler().createBlockStateMeta(index, if (value.itemType == Material.AIR) null else value)
    }
}