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
class OptBlockPosParser : MinecraftMetadataParser<Vector>() {

    override fun parse(value: Any): Vector {
        return Vector3Parser.parseToVector(value)
    }

    override fun createMeta(index: Int, value: Vector): MinecraftMeta {
        return metadataHandler().createOptBlockPosMeta(index, value)
    }
}