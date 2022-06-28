package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.common.api.MinecraftMeta
import ink.ptms.adyeshach.common.api.MinecraftMetadataParser
import ink.ptms.adyeshach.common.bukkit.data.VillagerData
import ink.ptms.adyeshach.common.util.getEnumOrNull
import org.bukkit.entity.Villager

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.parser.VillagerDataParser
 *
 * @author 坏黑
 * @since 2022/6/28 19:04
 */
class VillagerDataParser : MinecraftMetadataParser<VillagerData>() {

    override fun parse(value: Any): VillagerData {
        return if (value is Map<*, *>) {
            VillagerData(
                Villager.Type::class.java.getEnumOrNull(value["type"]!!) ?: Villager.Type.PLAINS,
                Villager.Profession::class.java.getEnumOrNull(value["profession"]!!) ?: Villager.Profession.NONE,
            )
        } else {
            value as VillagerData
        }
    }

    override fun createMeta(index: Int, value: VillagerData): MinecraftMeta {
        return metadataHandler().createVillagerDataMeta(index, value)
    }
}