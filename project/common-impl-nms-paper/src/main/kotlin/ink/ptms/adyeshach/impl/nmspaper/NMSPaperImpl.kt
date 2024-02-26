package ink.ptms.adyeshach.impl.nmspaper

import org.bukkit.craftbukkit.v1_20_R3.CraftWorld
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer
import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nmspaper.NMSPaperImpl
 *
 * @author 坏黑
 * @since 2024/2/27 01:45
 */
class NMSPaperImpl : NMSPaper() {

    override fun isChunkSent(player: Player, chunkX: Int, chunkZ: Int): Boolean {
        val playerChunkLoader = (player.world as CraftWorld).handle.playerChunkLoader
        return playerChunkLoader.isChunkSent((player as CraftPlayer).handle, chunkX, chunkZ)
    }
}