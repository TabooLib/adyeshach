package ink.ptms.adyeshach.impl.nms

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftPacketHandler
import org.bukkit.entity.Player
import taboolib.common.util.unsafeLazy
import taboolib.module.nms.sendPacket
import java.util.concurrent.ConcurrentHashMap

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.DefaultMinecraftPacketHandler
 *
 * @author 坏黑
 * @since 2022/6/28 00:11
 */
class DefaultMinecraftPacketHandler : MinecraftPacketHandler {

    val buffer = ConcurrentHashMap<Player, MutableList<BufferPacket>>()
    val operator by unsafeLazy { Adyeshach.api().getMinecraftAPI().getEntityOperator() }

    override fun sendPacket(player: List<Player>, packet: Any) {
        player.forEach { it.sendPacket(packet) }
    }

    override fun bufferMetadataPacket(player: List<Player>, id: Int, packet: MinecraftMeta) {
        player.forEach { buffer.computeIfAbsent(it) { mutableListOf() }.add(BufferPacket(id, packet)) }
    }

    override fun flush(player: List<Player>) {
        player.forEach { p -> buffer.remove(p)?.groupBy { it.id }?.forEach { b -> operator.updateEntityMetadata(p, b.key, b.value.map { it.packet }) } }
    }

    /** 缓存数据包 */
    class BufferPacket(val id: Int, val packet: MinecraftMeta)
}