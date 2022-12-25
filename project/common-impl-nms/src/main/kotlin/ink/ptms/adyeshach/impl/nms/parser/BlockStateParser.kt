package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import org.bukkit.Material
import org.bukkit.material.MaterialData
import taboolib.library.xseries.parseToMaterial
import taboolib.library.xseries.parseToXMaterial

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.parser.BlockStateParser
 *
 * @author 坏黑
 * @since 2022/6/28 19:01
 */
class BlockStateParser : MinecraftMetadataParser<MaterialData>() {

    override fun parse(value: Any): MaterialData {
        return when (value) {
            // 从配置中识别
            is Map<*, *> -> {
                MaterialData(value["type"].toString().replace("LEGACY_", "").parseToMaterial(), value["data"]!!.toByte())
            }
            // 从字符串中识别
            is String -> {
                val mat = value.parseToXMaterial()
                MaterialData(mat.parseMaterial(), mat.data)
            }
            // 其他
            else -> value as MaterialData
        }
    }

    override fun createMeta(index: Int, value: MaterialData): MinecraftMeta {
        return metadataHandler().createBlockStateMeta(index, if (value.itemType == Material.AIR) null else value)
    }
}