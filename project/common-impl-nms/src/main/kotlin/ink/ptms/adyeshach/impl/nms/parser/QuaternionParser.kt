package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import org.bukkit.util.Vector
import taboolib.common5.Quat
import taboolib.common5.cdouble
import taboolib.common5.cint

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.parser.VectorParser
 *
 * @author 坏黑
 * @since 2022/6/28 19:06
 */
class QuaternionParser : MinecraftMetadataParser<Quat>() {

    override fun parse(value: Any): Quat {
        return when (value) {
            // 从配置中识别
            is Map<*, *> -> {
                Quat(value["x"]!!.cdouble, value["y"]!!.cdouble, value["z"]!!.cdouble, value["w"]!!.cdouble)
            }
            // 从字符串中识别
            is String -> {
                val split = value.split(",")
                if (split.size != 3) {
                    throw IllegalArgumentException("Invalid Vector: $value")
                } else {
                    Quat(split[0].cdouble, split[1].cdouble, split[2].cdouble, split[3].cdouble)
                }
            }
            // 其他
            else -> value as Quat
        }
    }

    override fun createMeta(index: Int, value: Quat): MinecraftMeta {
        return metadataHandler().createQuaternionMeta(index, value)
    }
}