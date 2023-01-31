package ink.ptms.adyeshach.core

import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.MinecraftPacketHandler
 *
 * @author 坏黑
 * @since 2022/6/15 18:10
 */
interface MinecraftPacketHandler {

    /**
     * 发送数据包
     */
    fun sendPacket(player: List<Player>, packet: Any)
    fun sendPacket(player: Player, packet: Any) {
        sendPacket(listOf(player), packet)
    }

    /**
     * 将数据包存入缓冲区
     */
    fun bufferPacket(player: List<Player>, packet: Any)
    fun bufferPacket(player: Player, packet: Any) {
        bufferPacket(listOf(player), packet)
    }

    /**
     * 释放缓冲区
     */
    fun flush(player: List<Player>)
    fun flush(player: Player) {
        flush(listOf(player))
    }
}