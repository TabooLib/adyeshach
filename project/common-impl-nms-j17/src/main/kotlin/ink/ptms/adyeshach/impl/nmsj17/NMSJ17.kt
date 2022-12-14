package ink.ptms.adyeshach.impl.nmsj17

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.bukkit.data.GameProfile
import ink.ptms.adyeshach.core.bukkit.data.GameProfileAction
import taboolib.common.util.unsafeLazy
import taboolib.module.nms.nmsProxy
import java.util.*

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nmsj17.NMSJ17
 *
 * @author 坏黑
 * @since 2022/12/13 02:59
 */
@Suppress("UNCHECKED_CAST")
abstract class NMSJ17 {

    abstract fun entityTypeGetId(any: Any): Int

    abstract fun createPacketPlayOutEntityMetadata(entityId: Int, packedItems: List<MinecraftMeta>): Any

    abstract fun createClientboundPlayerInfoAddPacket(uuid: UUID, gameProfile: GameProfile): Any

    abstract fun createClientboundPlayerInfoUpdatePacket(uuid: UUID, gameProfile: GameProfile, actions: List<GameProfileAction>): Any

    abstract fun createClientboundPlayerInfoRemovePacket(uuid: UUID): Any

    abstract fun createClientboundPlayerInfoUpdatePacketAction(action: GameProfileAction): Any

    abstract fun createClientboundPlayerInfoUpdatePacketProfile(uuid: UUID, gameProfile: GameProfile): Any

    companion object {

        @JvmStatic
        val instance by unsafeLazy { nmsProxy<NMSJ17>() }
    }
}