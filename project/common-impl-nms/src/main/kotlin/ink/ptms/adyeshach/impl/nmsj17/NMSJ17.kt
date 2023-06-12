package ink.ptms.adyeshach.impl.nmsj17

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.bukkit.data.GameProfile
import ink.ptms.adyeshach.core.bukkit.data.GameProfileAction
import ink.ptms.adyeshach.core.entity.type.AdySniffer
import org.bukkit.entity.Cat
import org.bukkit.material.MaterialData
import org.bukkit.util.Vector
import taboolib.common.util.unsafeLazy
import taboolib.common5.Quat
import taboolib.module.nms.nmsProxy
import java.util.*

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nmsj17.NMSJ17
 *
 * @author 坏黑
 * @since 2022/12/13 02:59
 */
abstract class NMSJ17 {

    abstract fun entityTypeGetId(any: Any): Int

    abstract fun createVector3Meta(index: Int, value: Vector): Any

    abstract fun createQuaternionMeta(index: Int, quat: Quat): Any

    abstract fun createBlockStateMeta(index: Int, materialData: MaterialData): Any

    abstract fun createOptBlockStateMeta(index: Int, materialData: MaterialData?): Any

    abstract fun createCatVariantMeta(index: Int, type: Cat.Type): Any

    abstract fun createSnifferStateMeta(index: Int, type: AdySniffer.State): Any

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