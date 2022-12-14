package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import ink.ptms.adyeshach.core.bukkit.data.EmptyVector
import org.bukkit.util.Vector
import taboolib.common5.Coerce

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.parser.VectorParser
 *
 * @author 坏黑
 * @since 2022/6/28 19:06
 */
class BlockPosParser : MinecraftMetadataParser<Vector>() {

    override fun parse(value: Any): Vector {
        return if (value is Map<*, *>) {
            if (Coerce.toBoolean(value["empty"])) {
                EmptyVector()
            } else {
                Vector(value["x"]!!.toInt(), value["y"]!!.toInt(), value["z"]!!.toInt())
            }
        } else {
            value as Vector
        }
    }

    override fun createMeta(index: Int, value: Vector): MinecraftMeta {
        return metadataHandler().createBlockPosMeta(index, value)
    }
}