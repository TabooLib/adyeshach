package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import ink.ptms.adyeshach.core.bukkit.BukkitArmadilloState
import ink.ptms.adyeshach.core.util.getEnumOrNull

class ArmadilloStateParser : MinecraftMetadataParser<BukkitArmadilloState>() {
    override fun parse(value: Any): BukkitArmadilloState {
        return if (value is BukkitArmadilloState) value else BukkitArmadilloState::class.java.getEnumOrNull(value) ?: BukkitArmadilloState.IDLE
    }

    override fun createMeta(index: Int, value: BukkitArmadilloState): MinecraftMeta {
        return metadataHandler().createArmadilloStateMeta(index, value)
    }
}