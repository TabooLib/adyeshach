package ink.ptms.adyeshach.impl.nmspaper

import org.bukkit.entity.Player
import taboolib.common.util.unsafeLazy
import taboolib.module.nms.nmsProxy

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nmspaper.NMSPaper
 *
 * @author 坏黑
 * @since 2024/2/27 01:44
 */
abstract class NMSPaper {

    abstract fun isChunkSent(player: Player, chunkX: Int, chunkZ: Int): Boolean

    companion object {

        val instance by unsafeLazy { nmsProxy<NMSPaper>() }
    }
}