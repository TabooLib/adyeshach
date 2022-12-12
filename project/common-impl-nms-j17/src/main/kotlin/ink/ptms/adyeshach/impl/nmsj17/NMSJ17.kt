package ink.ptms.adyeshach.impl.nmsj17

import ink.ptms.adyeshach.common.api.MinecraftMeta
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata
import net.minecraft.network.syncher.DataWatcher

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nmsj17.NMSJ17
 *
 * @author 坏黑
 * @since 2022/12/13 02:59
 */
@Suppress("UNCHECKED_CAST")
object NMSJ17 {

    fun createPacketPlayOutEntityMetadata(entityId: Int, packedItems: List<MinecraftMeta>): Any {
        return PacketPlayOutEntityMetadata(entityId, packedItems.map { (it.source() as DataWatcher.Item<*>).value() })
    }
}