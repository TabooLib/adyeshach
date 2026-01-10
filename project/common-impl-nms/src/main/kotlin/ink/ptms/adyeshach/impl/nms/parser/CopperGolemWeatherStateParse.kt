package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import ink.ptms.adyeshach.core.bukkit.BukkitCopperWeatherState
import ink.ptms.adyeshach.core.util.getEnumOrNull

class CopperGolemWeatherStateParse : MinecraftMetadataParser<BukkitCopperWeatherState>() {
    override fun parse(value: Any): BukkitCopperWeatherState {
        return value as? BukkitCopperWeatherState ?: BukkitCopperWeatherState::class.java.getEnumOrNull(value) ?: BukkitCopperWeatherState.UNAFFECTED
    }

    override fun createMeta(index: Int, value: BukkitCopperWeatherState): MinecraftMeta {
        return metadataHandler().createGolemWeatherState(index, value)
    }
}