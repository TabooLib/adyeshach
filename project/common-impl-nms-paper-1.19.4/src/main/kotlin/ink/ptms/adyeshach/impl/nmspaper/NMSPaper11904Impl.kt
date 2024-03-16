package ink.ptms.adyeshach.impl.nmspaper

import net.minecraft.server.level.PlayerChunkMap
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer
import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nmspaper.NMSPaperImpl
 *
 * @author 坏黑
 * @since 2024/2/27 01:45
 */
class NMSPaper11904Impl : NMSPaper11904() {

    override fun isChunkSent(player: Player, playerChunkMap: Any, chunkX: Int, chunkZ: Int): Boolean {
        player as CraftPlayer
        playerChunkMap as PlayerChunkMap
        return playerChunkMap.playerChunkManager.isChunkSent(player.handle, chunkX, chunkZ)
    }
}