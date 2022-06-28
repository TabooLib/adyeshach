package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.common.api.MinecraftMeta
import ink.ptms.adyeshach.common.api.MinecraftMetadataParser
import ink.ptms.adyeshach.common.util.getEnumOrNull
import org.bukkit.entity.Cat

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.parser.CatVariantParser
 *
 * @author 坏黑
 * @since 2022/6/28 23:25
 */
class CatVariantParser : MinecraftMetadataParser<Cat.Type>() {

    override fun parse(value: Any): Cat.Type {
        return if (value is Cat.Type) value else Cat.Type::class.java.getEnumOrNull(value) ?: Cat.Type.TABBY
    }

    override fun createMeta(index: Int, value: Cat.Type): MinecraftMeta {
        return metadataHandler().createCatVariantMeta(index, value)
    }
}