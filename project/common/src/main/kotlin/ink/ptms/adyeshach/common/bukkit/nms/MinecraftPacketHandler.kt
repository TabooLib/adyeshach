package ink.ptms.adyeshach.common.bukkit.nms

import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.bukkit.nms.MinecraftPacketHandler
 *
 * @author 坏黑
 * @since 2022/6/15 18:10
 */
interface MinecraftPacketHandler {

    /**
     * 发送数据包
     */
    fun sendPacket(player: Player, packet: Any, vararg fields: Pair<String, Any?>)
}