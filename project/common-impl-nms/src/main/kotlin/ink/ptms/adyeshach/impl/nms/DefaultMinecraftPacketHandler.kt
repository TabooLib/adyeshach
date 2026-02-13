package ink.ptms.adyeshach.impl.nms

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftPacketHandler
import org.bukkit.entity.Player
import taboolib.common.util.unsafeLazy
import taboolib.module.nms.PacketSender
import taboolib.module.nms.sendBundlePacket
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.DefaultMinecraftPacketHandler
 *
 * 数据包缓冲与批量发送。sendPacket 将包写入 ConcurrentLinkedQueue，
 * flush 定时器通过 poll 取出并聚合为 BundlePacket 发送。
 *
 * 队列对象在玩家首次写入时创建，复用至玩家离线后由 cleanup 移除。
 *
 * @author 坏黑
 * @since 2022/6/28 00:11
 */
class DefaultMinecraftPacketHandler : MinecraftPacketHandler {

    val buffer = ConcurrentHashMap<Player, ConcurrentLinkedQueue<Any>>()
    val metaBuffer = ConcurrentHashMap<Player, ConcurrentLinkedQueue<BufferPacket>>()
    val metadataHandler by unsafeLazy { Adyeshach.api().getMinecraftAPI().getEntityMetadataHandler() }

    init {
        PacketSender.useMinecraftMethod()
    }

    override fun sendPacket(player: List<Player>, packet: Any) {
        player.forEach {
            buffer.getOrPut(it) { ConcurrentLinkedQueue() }.offer(packet)
        }
    }

    override fun bufferMetadataPacket(player: List<Player>, id: Int, packet: MinecraftMeta) {
        player.forEach {
            metaBuffer.getOrPut(it) { ConcurrentLinkedQueue() }.offer(BufferPacket(id, packet))
        }
    }

    override fun flush(player: List<Player>) {
        player.forEach { p ->
            // 处理普通数据包缓存
            // 队列对象保留在 map 中复用，仅通过 poll 取出元素。
            // 避免 remove 导致 sendPacket 的 getOrPut 拿到旧引用后 offer 到孤儿队列。
            drain(buffer[p])?.also { packets ->
                packets.chunked(MAX_BATCH_SIZE).forEach { batch ->
                    p.sendBundlePacket(batch)
                }
            }
            // 处理元数据缓存
            drain(metaBuffer[p])?.also { drained ->
                val packets = drained.groupBy { it.id }.map { (id, packets) ->
                    metadataHandler.createMetadataPacket(id, packets.map { it.packet })
                }
                packets.chunked(MAX_BATCH_SIZE).forEach { batch ->
                    p.sendBundlePacket(batch)
                }
            }
        }
    }

    /** 清理离线玩家的缓冲区 */
    override fun cleanup(player: Player) {
        buffer.remove(player)
        metaBuffer.remove(player)
    }

    /**
     * 批量取出队列中的元素。
     * poll 返回 null 表示队列为空，循环终止。MAX_DRAIN_SIZE 防止极端情况下的长时间阻塞。
     */
    private fun <T> drain(queue: ConcurrentLinkedQueue<T>?): List<T>? {
        if (queue == null) return null
        val first = queue.poll() ?: return null
        val list = ArrayList<T>(INITIAL_DRAIN_CAPACITY)
        list.add(first)
        var count = 1
        while (count < MAX_DRAIN_SIZE) {
            list.add(queue.poll() ?: break)
            count++
        }
        return list
    }

    /** 缓存数据包 */
    class BufferPacket(val id: Int, val packet: MinecraftMeta)

    companion object {

        private const val MAX_BATCH_SIZE = 1024

        // drain 初始容量，避免小量包时频繁扩容
        private const val INITIAL_DRAIN_CAPACITY = 32

        // drain 上限，防止极端情况下的长时间阻塞
        private const val MAX_DRAIN_SIZE = 4096
    }
}
