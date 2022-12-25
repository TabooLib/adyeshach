package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import ink.ptms.adyeshach.core.bukkit.data.EmptyVector
import org.bukkit.util.Vector
import taboolib.common5.cbool
import taboolib.common5.cint

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.parser.VectorParser
 *
 * @author 坏黑
 * @since 2022/6/28 19:06
 */
class BlockPosParser : MinecraftMetadataParser<Vector>() {

    override fun parse(value: Any): Vector {
        return when (value) {
            // 从配置中识别
            is Map<*, *> -> {
                if (value["empty"].cbool) {
                    EmptyVector()
                } else {
                    Vector(value["x"]!!.cint, value["y"]!!.cint, value["z"]!!.cint)
                }
            }
            // 从字符串中识别
            is String -> {
                val split = value.split(",")
                if (split.size != 3) {
                    throw IllegalArgumentException("Invalid Vector: $value")
                } else {
                    Vector(split[0].cint, split[1].cint, split[2].cint)
                }
            }
            // 其他
            else -> value as Vector
        }
    }

    override fun createMeta(index: Int, value: Vector): MinecraftMeta {
        return metadataHandler().createBlockPosMeta(index, value)
    }
}