package ink.ptms.adyeshach.impl.nms

import ink.ptms.adyeshach.common.api.MinecraftPacketHandler
import org.bukkit.entity.Player
import taboolib.module.nms.sendPacket

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.DefaultMinecraftPacketHandler
 *
 * @author 坏黑
 * @since 2022/6/28 00:11
 */
class DefaultMinecraftPacketHandler : MinecraftPacketHandler {

    override fun sendPacket(player: List<Player>, packet: Any) {
        player.forEach { it.sendPacket(packet) }
    }
}