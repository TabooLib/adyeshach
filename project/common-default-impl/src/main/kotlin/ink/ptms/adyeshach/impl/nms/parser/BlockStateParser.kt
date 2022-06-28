package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.common.api.MinecraftMeta
import ink.ptms.adyeshach.common.api.MinecraftMetadataParser
import org.bukkit.Material
import org.bukkit.material.MaterialData
import taboolib.library.xseries.XMaterial

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
            val id = value["type"].toString().replace("LEGACY_", "")
            val material = XMaterial.matchXMaterial(id).orElse(XMaterial.STONE).parseMaterial()!!
            MaterialData(material, value["data"]!!.toByte())
        } else {
            value as MaterialData
        }
    }

    override fun createMeta(index: Int, value: MaterialData): MinecraftMeta {
        return metadataHandler().createBlockStateMeta(index, if (value.itemType == Material.AIR) null else value)
    }
}