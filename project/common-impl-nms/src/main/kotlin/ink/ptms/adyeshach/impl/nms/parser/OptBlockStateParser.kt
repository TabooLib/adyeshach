package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import org.bukkit.Material
import org.bukkit.material.MaterialData
import taboolib.library.xseries.parseToMaterial
import taboolib.library.xseries.parseToXMaterial

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.parser.OptBlockStateParser
 *
 * @author 坏黑
 * @since 2022/6/28 19:01
 */
class OptBlockStateParser : MinecraftMetadataParser<MaterialData>() {

    override fun parse(value: Any): MaterialData {
        return BlockStateParser.parseToMaterialData(value)
    }

    override fun createMeta(index: Int, value: MaterialData): MinecraftMeta {
        return metadataHandler().createOptBlockStateMeta(index, if (value.itemType == Material.AIR) null else value)
    }
}