package ink.ptms.adyeshach.impl.nms.specific

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftScoreboardOperator
import ink.ptms.adyeshach.core.bukkit.*
import ink.ptms.adyeshach.core.bukkit.BukkitChickenType.*
import ink.ptms.adyeshach.core.bukkit.data.VillagerData
import ink.ptms.adyeshach.core.util.ifloor
import ink.ptms.adyeshach.impl.nms.*
import net.minecraft.EnumChatFormat
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.protocol.game.*
import net.minecraft.network.syncher.DataWatcher
import net.minecraft.network.syncher.DataWatcherObject
import net.minecraft.network.syncher.DataWatcherRegistry
import net.minecraft.world.entity.EntityPose
import net.minecraft.world.entity.PositionMoveRotation
import net.minecraft.world.entity.Relative
import net.minecraft.world.entity.animal.armadillo.Armadillo
import net.minecraft.world.phys.Vec3D
import net.minecraft.world.scores.Scoreboard
import net.minecraft.world.scores.ScoreboardTeam
import net.minecraft.world.scores.ScoreboardTeamBase
import org.bukkit.Art
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.craftbukkit.v1_21_R3.CraftArt
import org.bukkit.craftbukkit.v1_21_R3.CraftChunk
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftCat
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftVillager
import org.bukkit.craftbukkit.v1_21_R3.util.CraftChatMessage
import org.bukkit.craftbukkit.v1_21_R4.entity.CraftChicken
import org.bukkit.craftbukkit.v1_21_R4.entity.CraftPig
import org.bukkit.craftbukkit.v1_21_R4.entity.CraftWolf
import org.bukkit.entity.Cat
import org.bukkit.entity.Chicken
import org.bukkit.entity.Pig
import org.bukkit.entity.Wolf
import org.bukkit.material.MaterialData
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
        return CraftChatMessage.toJSON(compound as NMSIChatBaseComponent)
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
        val type = if (MinecraftVersion.versionId >= 12005) {
            BuiltInRegistries.ENTITY_TYPE.byId(entityType)
        } else {
            BuiltInRegistries.ENTITY_TYPE.get(entityType).get().value()
        }
        return PacketPlayOutSpawnEntity(entityId, uuid, location.x, location.y, location.z, pitch, yaw, type, data, Vec3D.ZERO, yhead)
    }

    override fun createEntityMetadata(entityId: Int, packedItems: List<MinecraftMeta>): Any {
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
            PositionMoveRotation(
                Vec3D(location.x, location.y, location.z),
                Vec3D(location.x, location.y, location.z),
                ifloor(yaw * 256.0 / 360.0).toFloat(),
                pitch.toFloat()
            ),
            setOf(Relative.X, Relative.Y, Relative.Z),
            onGround
        )
    }

    override fun createSyncPosition(entityId: Int, location: Location, onGround: Boolean): Any {
        return ClientboundEntityPositionSyncPacket(
            entityId, PositionMoveRotation(
                Vec3D(location.x, location.y, location.z),
                Vec3D(location.x, location.y, location.z),
                ifloor(location.yaw * 256.0 / 360.0).toFloat(),
                location.pitch
            ), onGround
        )
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

    override fun getPose(pose: BukkitPose): NMSEntityPose {
        return when (pose) {
            BukkitPose.SITTING -> EntityPose.SITTING
            BukkitPose.SLIDING -> EntityPose.SLIDING
            BukkitPose.SHOOTING -> EntityPose.SHOOTING
            BukkitPose.INHALING -> EntityPose.INHALING
            else -> TODO()
        }
    }

    override fun getVillagerType(type: VillagerData.Type): NMSVillagerType {
        val a = Registry.VILLAGER_TYPE.get(NamespacedKey.minecraft(type.name.lowercase()))
        return CraftVillager.CraftType.bukkitToMinecraft(a)
    }

    override fun getVillagerProfession(profession: VillagerData.Profession): NMSVillagerProfession {
        val a = Registry.VILLAGER_PROFESSION.get(NamespacedKey.minecraft(profession.name.lowercase()))
        return CraftVillager.CraftProfession.bukkitToMinecraft(a)
    }

    override fun getArtType(art: BukkitPaintings): Int {
        return CraftArt.bukkitToMinecraft(Registry.ART.get(NamespacedKey.minecraft(art.legacy.toString().lowercase())))!!.area()
    }

    override fun artBukkitToNotch(art: Art): Any {
        return CraftArt.bukkitToMinecraft(art).direct()
    }

    override fun createOptBlockStateMeta(index: Int, material: MaterialData?): Any {
        return DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.BYTE), index.toByte())
    }

    override fun createChickenMeta(index: Int, type: BukkitChickenType): Any {
        val value = when (type) {
            TEMPERATE -> Chicken.Variant.TEMPERATE
            WARM -> Chicken.Variant.WARM
            COLD -> Chicken.Variant.COLD
        }
        val variant = CraftChicken.CraftVariant.bukkitToMinecraftHolder(value)
        return DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.CHICKEN_VARIANT), variant)
    }

    override fun createArmadilloMeta(index: Int, value: BukkitArmadilloState): Any {
        return DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.ARMADILLO_STATE), Armadillo.a.valueOf(value.name))
    }

    override fun createPigVariantMeta(index: Int, value: BukkitPigVariant): Any {
        val type = when (value) {
            BukkitPigVariant.COLD -> Pig.Variant.COLD
            BukkitPigVariant.TEMPERATE -> Pig.Variant.TEMPERATE
            BukkitPigVariant.WARM -> Pig.Variant.WARM
        }
        return DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.PIG_VARIANT), CraftPig.CraftVariant.bukkitToMinecraftHolder(type))
    }

    override fun createWolfVariantMeta(index: Int, value: BukkitWolfVariant): Any {
        val type = when (value) {
            BukkitWolfVariant.PALE -> Wolf.Variant.PALE
            BukkitWolfVariant.SPOTTED -> Wolf.Variant.SPOTTED
            BukkitWolfVariant.SNOWY -> Wolf.Variant.SNOWY
            BukkitWolfVariant.BLACK -> Wolf.Variant.BLACK
            BukkitWolfVariant.ASHEN -> Wolf.Variant.ASHEN
            BukkitWolfVariant.RUSTY -> Wolf.Variant.RUSTY
            BukkitWolfVariant.WOODS -> Wolf.Variant.WOODS
            BukkitWolfVariant.CHESTNUT -> Wolf.Variant.CHESTNUT
            BukkitWolfVariant.STRIPED -> Wolf.Variant.STRIPED
        }
        return DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.WOLF_VARIANT), CraftWolf.CraftVariant.bukkitToMinecraftHolder(type))
    }

    override fun createCatVariantMeta(index: Int, value: BukkitCatType): Any {
        val variant = CraftCat.CraftType.bukkitToMinecraft(Cat.Type.valueOf(value.name))
        return DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.CAT_VARIANT), variant.direct())
    }

    private fun <T> T.direct(): Holder<T> {
        return Holder.direct(this)
    }
}