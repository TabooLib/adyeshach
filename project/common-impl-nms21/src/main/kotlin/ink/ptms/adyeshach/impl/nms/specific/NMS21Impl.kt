package ink.ptms.adyeshach.impl.nms.specific

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.impl.nms.NMSIChatBaseComponent
import ink.ptms.adyeshach.impl.nms.NMSPacketDataSerializer
import net.minecraft.core.IRegistryCustom
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.IChatBaseComponent
import net.minecraft.network.protocol.game.*
import net.minecraft.network.syncher.DataWatcher
import net.minecraft.world.entity.PositionMoveRotation
import net.minecraft.world.entity.Relative
import net.minecraft.world.phys.Vec3D
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_21_R3.CraftChunk
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.createDataSerializer
import java.util.*

class NMS21Impl : NMS21() {
    override fun createEntityHead(entityId: Int, yHeadRot: Byte): Any {
        return PacketPlayOutEntityHeadRotation::class.java.invokeConstructor(createDataSerializer {
            writeInt(entityId)
            writeByte(yHeadRot)
        }.build() as NMSPacketDataSerializer)
    }

    override fun toJson(compound: Any): String {
        return IChatBaseComponent.ChatSerializer.toJson(compound as NMSIChatBaseComponent, IRegistryCustom.EMPTY)
    }

    override fun createSpawnEntity(
        entityId: Int,
        uuid: UUID,
        location: Location,
        yaw: Float,
        pitch: Float,
        data: Int,
        entityType: Int,
        yhead: Double
    ): Any {

        val type = if (MinecraftVersion.versionId == 12101) {
            BuiltInRegistries.ENTITY_TYPE.byId(entityType)
        } else {
            BuiltInRegistries.ENTITY_TYPE.get(entityType).get().value()
        }
        return PacketPlayOutSpawnEntity(entityId, uuid, location.x, location.y, location.z, yaw, pitch, type, data, Vec3D.ZERO, yhead)
    }

    override fun createEntityMetadata(entityId: Int, packedItems: List<MinecraftMeta>): Any {
        return PacketPlayOutEntityMetadata(entityId, packedItems.map { (it.source() as DataWatcher.Item<*>).value() })
    }

    override fun createPassengers(entityId: Int, vararg passengers: Int): Any {
        return PacketPlayOutMount::class.java.invokeConstructor(createDataSerializer {
            writeVarInt(entityId)
            writeVarIntArray(passengers)
        }.build())
    }

    override fun getChunk(chunk: Any?): Any? {
        return (chunk as? CraftChunk)?.getHandle(net.minecraft.world.level.chunk.status.ChunkStatus.FULL)
    }

    override fun createTeleport(entityId: Int, location: Location, yaw: Byte, pitch: Byte, onGround: Boolean): Any {
        return PacketPlayOutEntityTeleport(
            entityId,
            PositionMoveRotation(Vec3D(location.x, location.y, location.z), Vec3D(location.x, location.y, location.z), yaw.toFloat(), pitch.toFloat()),
            setOf(Relative.X, Relative.Y, Relative.Z),
            onGround
        )
    }
}