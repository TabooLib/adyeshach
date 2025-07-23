package ink.ptms.adyeshach.impl.nms.specific

import org.bukkit.entity.Player
import taboolib.common.util.unsafeLazy
import taboolib.module.nms.nmsProxy

/**
 * Paper 1.19.4
 *
 * @author 坏黑
 * @since 2024/2/27 01:44
 */
abstract class NMS19p {

    abstract fun isChunkSent(player: Player, worldServer: Any, chunkX: Int, chunkZ: Int): Boolean

    companion object {

        val instance by unsafeLazy { nmsProxy<NMS19p>() }
    }
}