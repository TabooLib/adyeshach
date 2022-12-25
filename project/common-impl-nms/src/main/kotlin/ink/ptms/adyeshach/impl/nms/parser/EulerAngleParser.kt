package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import org.bukkit.util.EulerAngle
import taboolib.common5.cdouble

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.parser.EulerAngleParser
 *
 * @author 坏黑
 * @since 2022/6/28 18:35
 */
class EulerAngleParser : MinecraftMetadataParser<EulerAngle>() {

    override fun parse(value: Any): EulerAngle {
        return when (value) {
            // 从配置中识别
            is Map<*, *> -> {
                EulerAngle(value["x"]!!.cdouble, value["y"]!!.cdouble, value["z"]!!.cdouble)
            }
            // 从字符串中识别
            is String -> {
                val split = value.split(",")
                if (split.size != 3) {
                    throw IllegalArgumentException("Invalid EulerAngle: $value")
                } else {
                    EulerAngle(split[0].cdouble, split[1].cdouble, split[2].cdouble)
                }
            }
            // 其他
            else -> value as EulerAngle
        }
    }

    override fun createMeta(index: Int, value: EulerAngle): MinecraftMeta {
        return metadataHandler().createEulerAngleMeta(index, value)
    }
}