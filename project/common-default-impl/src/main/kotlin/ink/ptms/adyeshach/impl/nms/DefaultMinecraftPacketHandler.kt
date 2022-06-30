package ink.ptms.adyeshach.impl.nms

import ink.ptms.adyeshach.common.api.MinecraftPacketHandler
import org.bukkit.entity.Player
import taboolib.common.reflect.Reflex.Companion.setProperty
import taboolib.module.nms.sendPacket

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.DefaultMinecraftPacketHandler
 *
 * @author 坏黑
 * @since 2022/6/28 00:11
 */
class DefaultMinecraftPacketHandler : MinecraftPacketHandler {

    override fun sendPacket(player: Player, packet: Any, vararg fields: Pair<String, Any?>) {
        // 设置属性
        fields.filter { it.second != null }.forEach { (key, value) -> packet.setProperty(key, value) }
        // 发包
        player.sendPacket(packet)
    }
}