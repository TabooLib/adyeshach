package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import ink.ptms.adyeshach.core.bukkit.data.EmptyVector
import org.bukkit.util.Vector
import taboolib.common5.cbool
import taboolib.common5.cdouble
import taboolib.common5.cfloat
import taboolib.common5.cint

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.parser.VectorParser
 *
 * @author 坏黑
 * @since 2022/6/28 19:06
 */
class Vector3Parser : MinecraftMetadataParser<Vector>() {

    override fun parse(value: Any): Vector {
        return parseToVector(value)
    }

    override fun createMeta(index: Int, value: Vector): MinecraftMeta {
        return metadataHandler().createVector3Meta(index, value)
    }

    companion object {

        fun parseToVector(value: Any): Vector {
            return when (value) {
                // 从配置中识别
                is Map<*, *> -> {
                    if (value["empty"].cbool) {
                        EmptyVector()
                    } else {
                        Vector(value["x"]!!.cdouble, value["y"]!!.cdouble, value["z"]!!.cdouble)
                    }
                }
                // 从字符串中识别
                is String -> {
                    val split = value.split(',')
                    if (split.size != 3) {
                        throw IllegalArgumentException("Invalid Vector: $value")
                    } else {
                        Vector(split[0].cdouble, split[1].cdouble, split[2].cdouble)
                    }
                }
                // 其他
                else -> value as Vector
            }
        }
    }
}