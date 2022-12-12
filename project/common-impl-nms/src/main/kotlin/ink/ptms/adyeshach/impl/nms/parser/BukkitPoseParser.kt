package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.common.api.MinecraftMeta
import ink.ptms.adyeshach.common.api.MinecraftMetadataParser
import ink.ptms.adyeshach.common.bukkit.BukkitPose
import ink.ptms.adyeshach.common.util.getEnumOrNull

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.parser.BukkitPoseParser
 *
 * @author 坏黑
 * @since 2022/6/28 18:34
 */
class BukkitPoseParser : MinecraftMetadataParser<BukkitPose>() {

    override fun parse(value: Any): BukkitPose {
        return if (value is BukkitPose) value else BukkitPose::class.java.getEnumOrNull(value) ?: BukkitPose.STANDING
    }

    override fun createMeta(index: Int, value: BukkitPose): MinecraftMeta {
        return metadataHandler().createPoseMeta(index, value)
    }
}