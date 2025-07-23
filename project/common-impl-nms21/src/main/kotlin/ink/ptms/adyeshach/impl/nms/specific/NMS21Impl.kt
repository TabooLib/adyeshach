package ink.ptms.adyeshach.impl.nms.specific

import com.mojang.datafixers.util.Pair
import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftScoreboardOperator
import ink.ptms.adyeshach.impl.nms.NMSIChatBaseComponent
import ink.ptms.adyeshach.impl.nms.NMSPacketDataSerializer
import net.minecraft.EnumChatFormat
import net.minecraft.core.IRegistryCustom
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.IChatBaseComponent
import net.minecraft.network.protocol.game.*
import net.minecraft.network.syncher.DataWatcher
import net.minecraft.world.entity.EnumItemSlot
import net.minecraft.world.entity.PositionMoveRotation
import net.minecraft.world.entity.Relative
import net.minecraft.world.phys.Vec3D
import net.minecraft.world.scores.Scoreboard
import net.minecraft.world.scores.ScoreboardTeam
import net.minecraft.world.scores.ScoreboardTeamBase
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_21_R3.CraftChunk
import org.bukkit.craftbukkit.v1_21_R3.inventory.CraftItemStack
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.createDataSerializer
import java.util.*

class NMS21Impl : NMS21 {
    override fun createEntityHead(entityId: Int, yHeadRot: Byte): Any {
        return PacketPlayOutEntityHeadRotation::class.java.invokeConstructor(createDataSerializer {
            writeVarInt(entityId)
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
        return PacketPlayOutSpawnEntity(entityId, uuid, location.x, location.y, location.z, pitch, yaw, type, data, Vec3D.ZERO, yhead)
    }

    override fun createPacketPlayOutEntityMetadata(entityId: Int, packedItems: List<MinecraftMeta>): Any {
        return PacketPlayOutEntityMetadata(entityId, packedItems.map { (it.source() as DataWatcher.Item<*>).value() })
    }

    override fun createPassengers(entityId: Int, vararg passengers: Int): Any {
        return PacketPlayOutMount::class.java.invokeConstructor(createDataSerializer {
            writeVarInt(entityId)
            writeVarIntArray(passengers)
        }.build() as NMSPacketDataSerializer)
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

    override fun createEntityEquipment(entityId: Int, equipment: Map<EquipmentSlot, ItemStack>): Any {
        fun EquipmentSlot.toNMS(): EnumItemSlot {
            return when (this) {
                EquipmentSlot.HAND -> EnumItemSlot.MAINHAND
                EquipmentSlot.OFF_HAND -> EnumItemSlot.OFFHAND
                else -> EnumItemSlot.valueOf(name)
            }
        }
        return PacketPlayOutEntityEquipment(entityId, equipment.map { Pair(it.key.toNMS(), CraftItemStack.asNMSCopy(it.value)) })
    }

    override fun createTeam(team: MinecraftScoreboardOperator.Team, method: MinecraftScoreboardOperator.TeamMethod): Any {
        // ADD or CHANGE
        val parameters = if (method.ordinal in listOf(0, 2)) {
            val scoreTeam = ScoreboardTeam(Scoreboard(), team.name)
            // 改变颜色
            scoreTeam.color = EnumChatFormat.valueOf(team.color.name)
            // 设置
            scoreTeam.unpackOptions(2)
            // 是否隐藏名字
            scoreTeam.nameTagVisibility = if (team.nameTagVisible) {
                ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS
            } else {
                ScoreboardTeamBase.EnumNameTagVisibility.NEVER
            }
            // 是否启用碰撞箱
            scoreTeam.collisionRule = if (team.collision) {
                ScoreboardTeamBase.EnumTeamPush.ALWAYS
            } else {
                ScoreboardTeamBase.EnumTeamPush.NEVER
            }
            Optional.of(PacketPlayOutScoreboardTeam.b(scoreTeam))
        } else {
            Optional.empty()
        }
        // ADD or JOIN or LEAVE
        val players = if (method.ordinal in listOf(0, 3, 4)) {
            team.members
        } else {
            listOf()
        }
        return PacketPlayOutScoreboardTeam::class.java.invokeConstructor(team.name, method.ordinal, parameters, players)
    }
}