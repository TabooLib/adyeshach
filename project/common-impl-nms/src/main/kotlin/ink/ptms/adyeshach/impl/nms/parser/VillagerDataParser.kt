package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import ink.ptms.adyeshach.core.bukkit.data.VillagerData
import ink.ptms.adyeshach.core.util.getEnumOrNull

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.parser.VillagerDataParser
 *
 * @author 坏黑
 * @since 2022/6/28 19:04
 */
class VillagerDataParser : MinecraftMetadataParser<VillagerData>() {

    override fun parse(value: Any): VillagerData {
        return when (value) {
            // 从配置文件中识别
            is Map<*, *> -> {
                VillagerData(
                    VillagerData.Type::class.java.getEnumOrNull(value["type"]!!) ?: VillagerData.Type.PLAINS,
                    VillagerData.Profession::class.java.getEnumOrNull(value["profession"]!!) ?: VillagerData.Profession.NONE,
                )
            }
            // 从字符串中识别
            is String -> {
                val split = value.split(":")
                if (split.size != 2) {
                    throw IllegalArgumentException("Invalid VillagerData: $value")
                }
                VillagerData(
                    VillagerData.Type::class.java.getEnumOrNull(split[0]) ?: VillagerData.Type.PLAINS,
                    VillagerData.Profession::class.java.getEnumOrNull(split[1]) ?: VillagerData.Profession.NONE,
                )
            }
            // 其他
            else -> value as VillagerData
        }
    }

    override fun createMeta(index: Int, value: VillagerData): MinecraftMeta {
        return metadataHandler().createVillagerDataMeta(index, value)
    }
}