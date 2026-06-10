package ink.ptms.adyeshach.impl.nms.specific

import net.minecraft.server.level.WorldServer
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer
import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nmspaper.NMSPaperImpl
 *
 * @author 坏黑
 * @since 2024/2/27 01:45
 */
class NMS19pImpl : NMS19p() {

    override fun isChunkSent(player: Player, worldServer: Any, chunkX: Int, chunkZ: Int): Boolean {
        player as CraftPlayer
        worldServer as WorldServer
        return worldServer.k().b(chunkX, chunkZ)
    }
}