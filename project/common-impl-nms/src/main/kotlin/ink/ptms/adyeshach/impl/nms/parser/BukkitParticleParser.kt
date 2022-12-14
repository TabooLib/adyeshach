package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import ink.ptms.adyeshach.core.bukkit.BukkitParticles
import ink.ptms.adyeshach.core.util.getEnumOrNull

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.parser.BukkitParticleParser
 *
 * @author 坏黑
 * @since 2022/6/28 18:37
 */
class BukkitParticleParser : MinecraftMetadataParser<BukkitParticles>() {

    override fun parse(value: Any): BukkitParticles {
        return if (value is BukkitParticles) value else BukkitParticles::class.java.getEnumOrNull(value) ?: BukkitParticles.HAPPY_VILLAGER
    }

    override fun createMeta(index: Int, value: BukkitParticles): MinecraftMeta {
        return metadataHandler().createParticleMeta(index, value)
    }
}