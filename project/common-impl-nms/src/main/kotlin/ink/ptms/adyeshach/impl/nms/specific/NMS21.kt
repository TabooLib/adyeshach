package ink.ptms.adyeshach.impl.nms.specific

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftScoreboardOperator
import org.bukkit.Location
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import taboolib.common.util.unsafeLazy
import taboolib.module.nms.nmsProxy
import java.util.*

abstract class NMS21 {

    abstract fun createEntityHead(entityId: Int, yHeadRot: Byte): Any

    abstract fun toJson(compound: Any): String

    abstract fun createSpawnEntity(entityId: Int, uuid: UUID, location: Location, yaw: Float, pitch: Float, data: Int, entityType: Int, yhead: Double): Any

    abstract fun createPacketPlayOutEntityMetadata(entityId: Int, packedItems: List<MinecraftMeta>): Any

    abstract fun createPassengers(entityId: Int, vararg passengers: Int): Any

    abstract fun getChunk(chunk: Any?): Any?

    abstract fun createTeleport(entityId: Int, location: Location, yaw: Byte, pitch: Byte, onGround: Boolean): Any

    abstract fun createEntityEquipment(entityId: Int, equipment: Map<EquipmentSlot, ItemStack>): Any

    abstract fun createTeam(team: MinecraftScoreboardOperator.Team, method: MinecraftScoreboardOperator.TeamMethod): Any

    companion object {
        val instance by unsafeLazy { nmsProxy<NMS21>() }
    }
}