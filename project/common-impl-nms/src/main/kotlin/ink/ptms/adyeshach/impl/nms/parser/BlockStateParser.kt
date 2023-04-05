package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import org.bukkit.material.MaterialData
import taboolib.common5.cbyte
import taboolib.library.xseries.parseToMaterial
import taboolib.library.xseries.parseToXMaterial

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.parser.OptBlockStateParser
 *
 * @author 坏黑
 * @since 2022/6/28 19:01
 */
class BlockStateParser : MinecraftMetadataParser<MaterialData>() {

    override fun parse(value: Any): MaterialData {
        return parseToMaterialData(value)
    }

    override fun createMeta(index: Int, value: MaterialData): MinecraftMeta {
        return metadataHandler().createBlockStateMeta(index, value)
    }

    companion object {

        fun parseToMaterialData(value: Any): MaterialData {
            return when (value) {
                // 从配置中识别
                is Map<*, *> -> {
                    MaterialData(value["type"].toString().replace("LEGACY_", "").parseToMaterial(), value["data"]!!.cbyte)
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
    }
}