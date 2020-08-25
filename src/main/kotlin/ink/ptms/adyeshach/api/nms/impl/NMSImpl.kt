package ink.ptms.adyeshach.api.nms.impl

import com.google.common.base.Enums
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.bukkit.BukkitDirection
import ink.ptms.adyeshach.common.bukkit.BukkitPaintings
import ink.ptms.adyeshach.common.bukkit.BukkitParticles
import ink.ptms.adyeshach.common.bukkit.BukkitPose
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.element.PositionNull
import ink.ptms.adyeshach.common.entity.element.VillagerData
import io.izzel.taboolib.module.lite.SimpleEquip
import io.izzel.taboolib.module.lite.SimpleReflection
import io.izzel.taboolib.module.nms.impl.Position
import net.minecraft.server.v1_16_R1.*
import net.minecraft.server.v1_9_R2.WorldSettings
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftEntity
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftMob
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack
import org.bukkit.craftbukkit.v1_16_R1.util.CraftChatMessage
import org.bukkit.craftbukkit.v1_16_R1.util.CraftMagicNumbers
import org.bukkit.entity.Entity
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import org.bukkit.util.EulerAngle
import org.bukkit.util.Vector
import java.util.*

/**
 * @author Arasple
 * @date 2020/8/3 21:51
 */
class NMSImpl : NMS() {

    override fun spawnEntity(player: Player, entityType: EntityTypes, entityId: Int, uuid: UUID, location: Location) {
        sendPacket(
                player,
                PacketPlayOutSpawnEntity(),
                "a" to entityId,
                "b" to uuid,
                "c" to location.x,
                "d" to location.y,
                "e" to location.z,
                "f" to (location.yaw * 256.0f / 360.0f).toInt().toByte(),
                "g" to (location.pitch * 256.0f / 360.0f).toInt().toByte(),
                "k" to if (version <= 11300) entityType.bukkitId else getEntityTypeNMS(entityType)
        )
    }

    override fun spawnEntityLiving(player: Player, entityType: EntityTypes, entityId: Int, uuid: UUID, location: Location) {
        if (entityType == EntityTypes.ARMOR_STAND && version < 11300) {
            return spawnEntity(player, entityType, entityId, uuid, location)
        }
        sendPacket(
                player,
                PacketPlayOutSpawnEntityLiving(),
                "a" to entityId,
                "b" to uuid,
                "c" to when {
                    version >= 11400 -> IRegistry.ENTITY_TYPE.a(getEntityTypeNMS(entityType) as net.minecraft.server.v1_16_R1.EntityTypes<*>)
                    version == 11300 -> net.minecraft.server.v1_13_R2.IRegistry.ENTITY_TYPE.a(getEntityTypeNMS(entityType) as net.minecraft.server.v1_13_R2.EntityTypes<*>)
                    else -> entityType.bukkitId
                },
                "d" to location.x,
                "e" to location.y,
                "f" to location.z,
                "g" to 0,
                "h" to 0,
                "i" to 0,
                "j" to (location.yaw * 256.0f / 360.0f).toInt().toByte(),
                "k" to (location.pitch * 256.0f / 360.0f).toInt().toByte(),
                "l" to (location.yaw * 256.0f / 360.0f).toInt().toByte(),
                "m" to DataWatcher(null)
        )
    }

    override fun spawnNamedEntity(player: Player, entityId: Int, uuid: UUID, location: Location) {
        sendPacket(
                player,
                PacketPlayOutNamedEntitySpawn(),
                "a" to entityId,
                "b" to uuid,
                "c" to location.x,
                "d" to location.y,
                "e" to location.z,
                "f" to (location.yaw * 256 / 360).toInt().toByte(),
                "g" to (location.pitch * 256 / 360).toInt().toByte(),
                "h" to DataWatcher(null),
        )
    }

    override fun spawnEntityFallingBlock(player: Player, entityId: Int, uuid: UUID, location: Location, material: Material, data: Byte) {
        if (version >= 11300) {
            val block = SimpleReflection.getFieldValueChecked(Blocks::class.java, null, material.name, true)
            sendPacket(
                    player,
                    PacketPlayOutSpawnEntity(),
                    "a" to entityId,
                    "b" to uuid,
                    "c" to location.x,
                    "d" to location.y,
                    "e" to location.z,
                    "f" to (location.yaw * 256.0f / 360.0f).toInt().toByte(),
                    "g" to (location.pitch * 256.0f / 360.0f).toInt().toByte(),
                    "k" to getEntityTypeNMS(EntityTypes.FALLING_BLOCK),
                    "l" to Block.getCombinedId(((block ?: Blocks.STONE) as Block).blockData)
            )
        } else {
            sendPacket(
                    player,
                    PacketPlayOutSpawnEntity(),
                    "a" to entityId,
                    "b" to uuid,
                    "c" to location.x,
                    "d" to location.y,
                    "e" to location.z,
                    "f" to (location.yaw * 256.0f / 360.0f).toInt().toByte(),
                    "g" to (location.pitch * 256.0f / 360.0f).toInt().toByte(),
                    "k" to getEntityTypeNMS(EntityTypes.FALLING_BLOCK),
                    "l" to material.id + (data.toInt() shl 12)
            )
        }
    }

    override fun spawnEntityExperienceOrb(player: Player, entityId: Int, location: Location, amount: Int) {
        sendPacket(
                player,
                PacketPlayOutSpawnEntityExperienceOrb(),
                "a" to entityId,
                "b" to location.x,
                "c" to location.y,
                "d" to location.z,
                "e" to amount,
        )
    }

    override fun spawnEntityPainting(player: Player, entityId: Int, uuid: UUID, location: Location, direction: BukkitDirection, painting: BukkitPaintings) {
        if (version > 11300) {
            sendPacket(
                    player,
                    PacketPlayOutSpawnEntityPainting(),
                    "a" to entityId,
                    "b" to uuid,
                    "c" to getBlockPositionNMS(location),
                    "d" to Enums.getIfPresent(EnumDirection::class.java, direction.name).get(),
                    "e" to IRegistry.MOTIVE.a(getPaintingNMS(painting) as Paintings?)
            )
        } else {
            sendPacket(
                    player,
                    net.minecraft.server.v1_9_R2.PacketPlayOutSpawnEntityPainting(),
                    "a" to entityId,
                    "b" to uuid,
                    "c" to getBlockPositionNMS(location),
                    "d" to Enums.getIfPresent(net.minecraft.server.v1_9_R2.EnumDirection::class.java, direction.name).get(),
                    "e" to getPaintingNMS(painting)
            )
        }
    }

    override fun addPlayerInfo(player: Player, uuid: UUID, name: String, ping: Int, gameMode: GameMode, texture: Array<String>) {
        val profile = GameProfile(uuid, name)
        if (texture.size == 2) {
            profile.properties.put("textures", Property("textures", texture[0], texture[1]))
        }
        if (version >= 11000) {
            val infoData = PacketPlayOutPlayerInfo()
            sendPacket(
                    player,
                    infoData,
                    "a" to PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,
                    "b" to listOf(infoData.PlayerInfoData(profile, ping, Enums.getIfPresent(EnumGamemode::class.java, gameMode.name).or(EnumGamemode.NOT_SET), CraftChatMessage.fromString(name).firstOrNull()))
            )
        } else {
            val infoData = net.minecraft.server.v1_9_R2.PacketPlayOutPlayerInfo()
            sendPacket(
                    player,
                    infoData,
                    "a" to PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,
                    "b" to listOf(infoData.PlayerInfoData(profile, ping, Enums.getIfPresent(WorldSettings.EnumGamemode::class.java, gameMode.name).or(WorldSettings.EnumGamemode.NOT_SET), org.bukkit.craftbukkit.v1_9_R2.util.CraftChatMessage.fromString(name).firstOrNull()))
            )
        }
    }

    override fun removePlayerInfo(player: Player, uuid: UUID) {
        val infoData = PacketPlayOutPlayerInfo()
        sendPacket(
                player,
                infoData,
                "a" to PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,
                "b" to listOf(infoData.PlayerInfoData(GameProfile(uuid, ""), -1, null, null))
        )
    }

    override fun destroyEntity(player: Player, entityId: Int) {
        sendPacket(player, PacketPlayOutEntityDestroy(entityId))
    }

    override fun teleportEntity(player: Player, entityId: Int, location: Location) {
        sendPacket(
                player,
                PacketPlayOutEntityTeleport(),
                "a" to entityId,
                "b" to location.x,
                "c" to location.y,
                "d" to location.z,
                "e" to (location.yaw * 256 / 360).toInt().toByte(),
                "f" to (location.pitch * 256 / 360).toInt().toByte(),
                "g" to false // onGround
        )
    }

    override fun relMoveEntity(player: Player, entityId: Int, x: Double, y: Double, z: Double) {
        if (version >= 11400) {
            sendPacket(player, PacketPlayOutEntity.PacketPlayOutRelEntityMove(entityId, (x * 4096).toInt().toShort(), (y * 4096).toInt().toShort(), (z * 4096).toInt().toShort(), true))
        } else {
            sendPacket(player, net.minecraft.server.v1_13_R2.PacketPlayOutEntity.PacketPlayOutRelEntityMove(entityId, x.toLong(), y.toLong(), z.toLong(), true))
        }
    }

    override fun updateEntityVelocity(player: Player, entityId: Int, vector: Vector) {
        if (version >= 11400) {
            sendPacket(player, PacketPlayOutEntityVelocity(entityId, Vec3D(vector.x, vector.y, vector.z)))
        } else {
            sendPacket(player, net.minecraft.server.v1_12_R1.PacketPlayOutEntityVelocity(entityId, vector.x, vector.y, vector.z))
        }
    }

    override fun setHeadRotation(player: Player, entityId: Int, yaw: Float, pitch: Float) {
        sendPacket(
                player,
                PacketPlayOutEntityHeadRotation(),
                "a" to entityId,
                "b" to MathHelper.d(yaw * 256.0f / 360.0f).toByte()
        )
        sendPacket(
                player,
                PacketPlayOutEntity.PacketPlayOutEntityLook(
                        entityId,
                        MathHelper.d(yaw * 256.0f / 360.0f).toByte(),
                        MathHelper.d(pitch * 256.0f / 360.0f).toByte(),
                        true
                )
        )
    }

    override fun updateEquipment(player: Player, entityId: Int, slot: EquipmentSlot, itemStack: ItemStack) {
        if (version >= 11600) {
            sendPacket(player, PacketPlayOutEntityEquipment(entityId, listOf(com.mojang.datafixers.util.Pair(EnumItemSlot.fromName(SimpleEquip.fromBukkit(slot).nms), CraftItemStack.asNMSCopy(itemStack)))))
        } else if (version >= 11300) {
            sendPacket(player, net.minecraft.server.v1_13_R2.PacketPlayOutEntityEquipment(entityId, net.minecraft.server.v1_13_R2.EnumItemSlot.fromName(SimpleEquip.fromBukkit(slot).nms), org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack.asNMSCopy(itemStack)))
        } else {
            sendPacket(player, net.minecraft.server.v1_12_R1.PacketPlayOutEntityEquipment(entityId, net.minecraft.server.v1_12_R1.EnumItemSlot.a(SimpleEquip.fromBukkit(slot).nms), org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack.asNMSCopy(itemStack)))
        }
    }

    override fun updatePassengers(player: Player, entityId: Int, vararg passengers: Int) {
        sendPacket(player, PacketPlayOutMount(), "a" to entityId, "b" to passengers)
    }

    override fun updateEntityMetadata(player: Player, entityId: Int, vararg objects: Any) {
        sendPacket(player, PacketPlayOutEntityMetadata(), "a" to entityId, "b" to objects.map { it as DataWatcher.Item<*> }.toList())
    }

    override fun getMetaEntityInt(index: Int, value: Int): Any {
        return DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.b), value)
    }

    override fun getMetaEntityFloat(index: Int, value: Float): Any {
        return DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.c), value)
    }

    override fun getMetaEntityString(index: Int, value: String): Any {
        return DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.d), value)
    }

    override fun getMetaEntityBoolean(index: Int, value: Boolean): Any {
        return if (version >= 11300) {
            DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.i), value)
        } else {
            net.minecraft.server.v1_11_R1.DataWatcher.Item(net.minecraft.server.v1_11_R1.DataWatcherObject(index, net.minecraft.server.v1_11_R1.DataWatcherRegistry.h), value)
        }
    }

    override fun getMetaEntityParticle(index: Int, value: BukkitParticles): Any {
        return DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.j), getParticleNMS(value) as ParticleParam?)
    }

    override fun getMetaEntityByte(index: Int, value: Byte): Any {
        return DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.a), value)
    }

    override fun getMetaEntityVector(index: Int, value: EulerAngle): Any {
        return if (version >= 11300) {
            DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.k), Vector3f(value.x.toFloat(), value.y.toFloat(), value.z.toFloat()))
        } else {
            net.minecraft.server.v1_12_R1.DataWatcher.Item(net.minecraft.server.v1_12_R1.DataWatcherObject(index, net.minecraft.server.v1_12_R1.DataWatcherRegistry.i), net.minecraft.server.v1_12_R1.Vector3f(value.x.toFloat(), value.y.toFloat(), value.z.toFloat()))
        }
    }

    override fun getMetaEntityPosition(index: Int, value: Position?): Any {
        return if (version >= 11300) {
            DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.m), Optional.ofNullable(if (value == null || value is PositionNull) null else BlockPosition(value.x, value.y, value.z)))
        } else {
            net.minecraft.server.v1_12_R1.DataWatcher.Item(
                    net.minecraft.server.v1_12_R1.DataWatcherObject(index, net.minecraft.server.v1_12_R1.DataWatcherRegistry.k),
                    com.google.common.base.Optional.fromNullable(if (value == null || value is PositionNull) null else net.minecraft.server.v1_12_R1.BlockPosition(value.x, value.y, value.z))
            )
        }
    }

    override fun getMetaEntityBlockData(index: Int, value: MaterialData?): Any {
        return if (version >= 11300) {
            DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.h), Optional.ofNullable(if (value == null) null else CraftMagicNumbers.getBlock(value)))
        } else {
            net.minecraft.server.v1_12_R1.DataWatcher.Item(net.minecraft.server.v1_12_R1.DataWatcherObject(index, net.minecraft.server.v1_12_R1.DataWatcherRegistry.g), com.google.common.base.Optional.fromNullable(if (value == null) null else org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers.getBlock(value.itemType).fromLegacyData(value.data.toInt())))
        }
    }

    override fun getMetaEntityChatBaseComponent(index: Int, name: String?): Any {
        return if (version >= 11300) {
            DataWatcher.Item<Optional<IChatBaseComponent>>(DataWatcherObject(index, DataWatcherRegistry.f), Optional.ofNullable(if (name == null) null else CraftChatMessage.fromString(name).first()))
        } else {
            net.minecraft.server.v1_12_R1.DataWatcher.Item(net.minecraft.server.v1_12_R1.DataWatcherObject(index, net.minecraft.server.v1_12_R1.DataWatcherRegistry.d), name ?: "")
        }
    }

    override fun getMetaItem(index: Int, itemStack: ItemStack): Any {
        return when {
            version >= 11300 -> {
                DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.g), CraftItemStack.asNMSCopy(itemStack))
            }
            version >= 11200 -> {
                net.minecraft.server.v1_12_R1.DataWatcher.Item(net.minecraft.server.v1_12_R1.DataWatcherObject(6, net.minecraft.server.v1_12_R1.DataWatcherRegistry.f), org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack.asNMSCopy(itemStack))
            }
            else -> {
                return net.minecraft.server.v1_9_R2.DataWatcher.Item(net.minecraft.server.v1_9_R2.DataWatcherObject(6, net.minecraft.server.v1_9_R2.DataWatcherRegistry.f), com.google.common.base.Optional.fromNullable(org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack.asNMSCopy(itemStack)))
            }
        }
    }

    override fun getMetaVillagerData(index: Int, villagerData: VillagerData): Any {
        return DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.q), VillagerData(when (villagerData.type) {
            Villager.Type.DESERT -> VillagerType.DESERT
            Villager.Type.JUNGLE -> VillagerType.JUNGLE
            Villager.Type.PLAINS -> VillagerType.PLAINS
            Villager.Type.SAVANNA -> VillagerType.SAVANNA
            Villager.Type.SNOW -> VillagerType.SNOW
            Villager.Type.SWAMP -> VillagerType.SWAMP
            Villager.Type.TAIGA -> VillagerType.TAIGA
        }, when (villagerData.profession) {
            Villager.Profession.NONE -> VillagerProfession.NONE
            Villager.Profession.ARMORER -> VillagerProfession.ARMORER
            Villager.Profession.BUTCHER -> VillagerProfession.BUTCHER
            Villager.Profession.CARTOGRAPHER -> VillagerProfession.CARTOGRAPHER
            Villager.Profession.CLERIC -> VillagerProfession.CLERIC
            Villager.Profession.FARMER -> VillagerProfession.FARMER
            Villager.Profession.FISHERMAN -> VillagerProfession.FISHERMAN
            Villager.Profession.FLETCHER -> VillagerProfession.FLETCHER
            Villager.Profession.LEATHERWORKER -> VillagerProfession.LEATHERWORKER
            Villager.Profession.LIBRARIAN -> VillagerProfession.LIBRARIAN
            Villager.Profession.MASON -> VillagerProfession.MASON
            Villager.Profession.NITWIT -> VillagerProfession.NITWIT
            Villager.Profession.SHEPHERD -> VillagerProfession.SHEPHERD
            Villager.Profession.TOOLSMITH -> VillagerProfession.TOOLSMITH
            Villager.Profession.WEAPONSMITH -> VillagerProfession.WEAPONSMITH
        }, 1))
    }

    override fun getMetaEntityPose(index: Int, pose: BukkitPose): Any {
        return DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.s), Enums.getIfPresent(EntityPose::class.java, pose.name).or(EntityPose.STANDING))
    }

    override fun getEntityTypeNMS(entityTypes: EntityTypes): Any {
        return if (version >= 11300) {
            SimpleReflection.getFieldValueChecked(net.minecraft.server.v1_16_R1.EntityTypes::class.java, null, entityTypes.internalName ?: entityTypes.name, true)
        } else {
            entityTypes.bukkitId
        }
    }

    override fun getBlockPositionNMS(location: Location): Any {
        return BlockPosition(location.blockX, location.blockY, location.blockZ)
    }

    override fun getPaintingNMS(bukkitPaintings: BukkitPaintings): Any {
        return if (version >= 11300) {
            SimpleReflection.getFieldValueChecked(Paintings::class.java, null, bukkitPaintings.index.toString(), true)
        } else {
            bukkitPaintings.legacy
        }
    }

    override fun getParticleNMS(bukkitParticles: BukkitParticles): Any {
        return when {
            version == 11300 -> {
                val p = IRegistry.PARTICLE_TYPE.get(MinecraftKey(bukkitParticles.name.toLowerCase())) ?: net.minecraft.server.v1_13_R2.Particles.y
                if (p is net.minecraft.server.v1_13_R2.Particle<*>) {
                    p.f()
                } else {
                    p
                }
            }
            version >= 11400 -> {
                val p = SimpleReflection.getFieldValueChecked(Particles::class.java, null, bukkitParticles.name, true) ?: Particles.FLAME
                if (p is Particle<*>) {
                    p.d()
                } else {
                    p
                }
            }
            else -> 0
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun getNavigationPathList(mob: Mob, location: Location): MutableList<Position> {
        val pathEntity = (mob as CraftMob).handle.navigation.a(BlockPosition(location.blockX, location.blockY, location.blockZ), 1) ?: return mutableListOf()
        val pathPoint = SimpleReflection.getFieldValueChecked(PathEntity::class.java, pathEntity, "a", true) as List<PathPoint>
        return pathPoint.map { Position(it.a, it.b, it.c) }.toMutableList()
    }

    override fun getEntityDataWatcher(entity: Entity): Any {
        return (entity as CraftEntity).handle.dataWatcher
    }

    override fun toBlockId(materialData: MaterialData): Int {
        return if (version >= 11300) {
            val block = SimpleReflection.getFieldValueChecked(Blocks::class.java, null, materialData.itemType.name, true)
            Block.getCombinedId(((block ?: Blocks.STONE) as Block).blockData)
        } else {
            materialData.itemType.id + (materialData.data.toInt() shl 12)
        }
    }

    override fun getEntity(world: World, id: Int): Entity? {
        return (world as CraftWorld).handle.getEntity(id)?.bukkitEntity
    }

    override fun parseVec3d(obj: Any): Vector {
        return Vector((obj as Vec3D).x, obj.y, obj.z)
    }

}