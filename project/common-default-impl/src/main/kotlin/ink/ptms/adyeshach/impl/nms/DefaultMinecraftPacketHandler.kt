package ink.ptms.adyeshach.impl.nms

import ink.ptms.adyeshach.common.api.MinecraftPacketHandler
import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.DefaultMinecraftPacketHandler
 *
 * @author 坏黑
 * @since 2022/6/28 00:11
 */
class DefaultMinecraftPacketHandler : MinecraftPacketHandler {

    override fun sendPacket(player: Player, packet: Any, vararg fields: Pair<String, Any?>) {
        TODO("Not yet implemented")
    }
}