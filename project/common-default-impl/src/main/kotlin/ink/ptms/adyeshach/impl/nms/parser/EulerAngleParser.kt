package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.common.api.MinecraftMeta
import ink.ptms.adyeshach.common.api.MinecraftMetadataParser
import org.bukkit.util.EulerAngle

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.parser.EulerAngleParser
 *
 * @author 坏黑
 * @since 2022/6/28 18:35
 */
class EulerAngleParser : MinecraftMetadataParser<EulerAngle>() {

    override fun parse(value: Any): EulerAngle {
        return if (value is Map<*, *>) EulerAngle(value["x"]!!.toDouble(), value["y"]!!.toDouble(), value["z"]!!.toDouble()) else value as EulerAngle
    }

    override fun createMeta(index: Int, value: EulerAngle): MinecraftMeta {
        return metadataHandler().createEulerAngleMeta(index, value)
    }
}