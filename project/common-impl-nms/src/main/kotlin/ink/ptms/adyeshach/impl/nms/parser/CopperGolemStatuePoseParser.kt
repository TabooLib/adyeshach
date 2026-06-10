package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import ink.ptms.adyeshach.core.bukkit.BukkitCopperGolemStatuePose
import ink.ptms.adyeshach.core.util.getEnumOrNull

class CopperGolemStatuePoseParser : MinecraftMetadataParser<BukkitCopperGolemStatuePose>() {
    override fun parse(value: Any): BukkitCopperGolemStatuePose {
        return value as? BukkitCopperGolemStatuePose ?: BukkitCopperGolemStatuePose::class.java.getEnumOrNull(value) ?: BukkitCopperGolemStatuePose.IDLE
    }

    override fun createMeta(index: Int, value: BukkitCopperGolemStatuePose): MinecraftMeta {
        return metadataHandler().createGolemStatuePose(index, value)
    }
}