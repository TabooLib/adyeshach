package ink.ptms.adyeshach.impl.nms

import com.google.common.base.Optional

// Universal

typealias NMSPacketPlayOutSpawnEntity = net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity

// 从 1.19+ 移除
typealias NMSPacketPlayOutSpawnEntityLiving = net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving

typealias NMSPacketPlayOutSpawnEntityPlayer = net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn

typealias NMSPacketPlayOutSpawnEntityExperienceOrb = net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityExperienceOrb

// 从 1.19+ 移除
typealias NMSPacketPlayOutSpawnEntityPainting = net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityPainting

typealias NMSPacketPlayOutPlayerInfo = net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo

typealias NMSPacketPlayOutPlayerInfoEnumPlayerInfoAction = net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo.EnumPlayerInfoAction

typealias NMSPacketPlayOutPlayerInfoPlayerInfoData = net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo.PlayerInfoData

typealias NMSPacketPlayOutEntityDestroy = net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy

typealias NMSPacketPlayOutEntityTeleport = net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport

typealias NMSPacketPlayOutEntityLook = net.minecraft.network.protocol.game.PacketPlayOutEntity.PacketPlayOutEntityLook

typealias NMSPacketPlayOutRelEntityMove = net.minecraft.network.protocol.game.PacketPlayOutEntity.PacketPlayOutRelEntityMove

typealias NMSPacketPlayOutRelEntityMoveLook = net.minecraft.network.protocol.game.PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook

typealias NMSPacketPlayOutEntityVelocity = net.minecraft.network.protocol.game.PacketPlayOutEntityVelocity

typealias NMSPacketPlayOutEntityHeadRotation = net.minecraft.network.protocol.game.PacketPlayOutEntityHeadRotation

typealias NMSPacketPlayOutEntityEquipment = net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment

typealias NMSPacketPlayOutMount = net.minecraft.network.protocol.game.PacketPlayOutMount

typealias NMSPacketPlayOutEntityMetadata = net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata

typealias NMSPacketPlayOutAnimation = net.minecraft.network.protocol.game.PacketPlayOutAnimation

typealias NMSPacketPlayOutAttachEntity = net.minecraft.network.protocol.game.PacketPlayOutAttachEntity

typealias NMSPacketPlayOutScoreboardTeam = net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam

typealias NMSPacketPlayOutEntityEffect = net.minecraft.network.protocol.game.PacketPlayOutEntityEffect

typealias NMSPacketDataSerializer = net.minecraft.network.PacketDataSerializer

typealias NMSIRegistry<T> = net.minecraft.core.IRegistry<T>

typealias NMSBuiltInRegistries = net.minecraft.core.registries.BuiltInRegistries

typealias NMSBlock = net.minecraft.world.level.block.Block

typealias NMSBlocks = net.minecraft.world.level.block.Blocks

typealias NMSEnumDirection = net.minecraft.core.EnumDirection

typealias NMSEnumGameMode = net.minecraft.world.level.EnumGamemode

typealias NMSEnumItemSlot = net.minecraft.world.entity.EnumItemSlot

typealias NMSBlockPosition = net.minecraft.core.BlockPosition

typealias NMSBlockData = net.minecraft.world.level.block.state.BlockBase.BlockData

typealias NMSEntityPose = net.minecraft.world.entity.EntityPose

typealias NMSEntityTypes<T> = net.minecraft.world.entity.EntityTypes<T>

typealias NMSIBlockAccess = net.minecraft.world.level.IBlockAccess

typealias NMSVec3D = net.minecraft.world.phys.Vec3D

typealias NMSVector3f = net.minecraft.core.Vector3f

typealias NMSVillagerType = net.minecraft.world.entity.npc.VillagerType

typealias NMSVillagerProfession = net.minecraft.world.entity.npc.VillagerProfession

typealias NMSVillagerData = net.minecraft.world.entity.npc.VillagerData

typealias NMSDataWatcherObject<T> = net.minecraft.network.syncher.DataWatcherObject<T>

typealias NMSDataWatcher = net.minecraft.network.syncher.DataWatcher

typealias NMSDataWatcherItem<T> = net.minecraft.network.syncher.DataWatcher.Item<T>

typealias NMSDataWatcherRegistry = net.minecraft.network.syncher.DataWatcherRegistry

typealias NMSIChatBaseComponent = net.minecraft.network.chat.IChatBaseComponent

typealias NMSChatSerializer = net.minecraft.network.chat.IChatBaseComponent.ChatSerializer

typealias NMSParticleParam = net.minecraft.core.particles.ParticleParam

typealias NMSCatVariant = net.minecraft.world.entity.animal.CatVariant

typealias NMSFrogVariant = net.minecraft.world.entity.animal.FrogVariant

typealias NMSMathHelper = net.minecraft.util.MathHelper

typealias NMSMaterial = net.minecraft.world.level.material.Material

typealias CraftArt19 = org.bukkit.craftbukkit.v1_19_R2.CraftArt

typealias CraftChunk19 = org.bukkit.craftbukkit.v1_19_R3.CraftChunk

typealias CraftEntity19 = org.bukkit.craftbukkit.v1_19_R2.entity.CraftEntity

typealias CraftItemStack19 = org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack

typealias CraftMagicNumbers19 = org.bukkit.craftbukkit.v1_19_R2.util.CraftMagicNumbers

typealias CraftTropicalFishPattern19 = org.bukkit.craftbukkit.v1_19_R2.entity.CraftTropicalFish.CraftPattern

typealias CraftChatMessage19 = org.bukkit.craftbukkit.v1_19_R2.util.CraftChatMessage

typealias CraftWorld19 = org.bukkit.craftbukkit.v1_19_R2.CraftWorld

// 1.16

typealias NMS16PacketPlayOutSpawnEntity = net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntity

typealias NMS16PacketPlayOutSpawnEntityPlayer = net.minecraft.server.v1_16_R3.PacketPlayOutNamedEntitySpawn

typealias NMS16PacketPlayOutSpawnEntityLiving = net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntityLiving

typealias NMS16PacketPlayOutSpawnEntityExperienceOrb = net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntityExperienceOrb

typealias NMS16PacketPlayOutSpawnEntityPainting = net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntityPainting

typealias NMS16PacketPlayOutPlayerInfo = net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo

typealias NMS16PacketPlayOutPlayerInfoEnumPlayerInfoAction = net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction

typealias NMS16PacketPlayOutPlayerInfoPlayerInfoData = net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo.PlayerInfoData

typealias NMS16PacketPlayOutEntityDestroy = net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy

typealias NMS16PacketPlayOutEntityTeleport = net.minecraft.server.v1_16_R3.PacketPlayOutEntityTeleport

typealias NMS16PacketPlayOutRelEntityMove = net.minecraft.server.v1_16_R3.PacketPlayOutEntity.PacketPlayOutRelEntityMove

typealias NMS16PacketPlayOutEntityVelocity = net.minecraft.server.v1_16_R3.PacketPlayOutEntityVelocity

typealias NMS16PacketPlayOutEntityHeadRotation = net.minecraft.server.v1_16_R3.PacketPlayOutEntityHeadRotation

typealias NMS16PacketPlayOutEntityLook = net.minecraft.server.v1_16_R3.PacketPlayOutEntity.PacketPlayOutEntityLook

typealias NMS16PacketPlayOutEntityEquipment = net.minecraft.server.v1_16_R1.PacketPlayOutEntityEquipment

typealias NMS16PacketPlayOutMount = net.minecraft.server.v1_16_R3.PacketPlayOutMount

typealias NMS16PacketPlayOutEntityMetadata = net.minecraft.server.v1_16_R3.PacketPlayOutEntityMetadata

typealias NMS16PacketPlayOutAnimation = net.minecraft.server.v1_16_R3.PacketPlayOutAnimation

typealias NMS16PacketPlayOutAttachEntity = net.minecraft.server.v1_16_R3.PacketPlayOutAttachEntity

typealias NMS16PacketPlayOutScoreboardTeam = net.minecraft.server.v1_16_R3.PacketPlayOutScoreboardTeam

typealias NMS16PacketDataSerializer = net.minecraft.server.v1_16_R3.PacketDataSerializer

typealias NMS16EntityTypes<T> = net.minecraft.server.v1_16_R3.EntityTypes<T>

typealias NMS16RegistryBlocks<T> = net.minecraft.server.v1_16_R3.RegistryBlocks<T>

typealias NMS16IRegistry<T> = net.minecraft.server.v1_16_R3.IRegistry<T>

typealias NMS16Block = net.minecraft.server.v1_16_R3.Block

typealias NMS16Blocks = net.minecraft.server.v1_16_R3.Blocks

typealias NMS16EnumDirection = net.minecraft.server.v1_16_R3.EnumDirection

typealias NMS16EnumGameMode = net.minecraft.server.v1_16_R3.EnumGamemode

typealias NMS16EnumItemSlot = net.minecraft.server.v1_16_R3.EnumItemSlot

typealias NMS16BlockPosition = net.minecraft.server.v1_16_R3.BlockPosition

typealias NMS16Paintings = net.minecraft.server.v1_16_R3.Paintings

typealias NMS16Particles = net.minecraft.server.v1_16_R3.Particles

typealias NMS16Vector3f = net.minecraft.server.v1_16_R3.Vector3f

typealias NMS16EntityPose = net.minecraft.server.v1_16_R3.EntityPose

typealias NMS16VillagerType = net.minecraft.server.v1_16_R3.VillagerType

typealias NMS16VillagerProfession = net.minecraft.server.v1_16_R3.VillagerProfession

typealias NMS16DataWatcherObject<T> = net.minecraft.server.v1_16_R3.DataWatcherObject<T>

typealias NMS16DataWatcher = net.minecraft.server.v1_16_R3.DataWatcher

typealias NMS16DataWatcherItem<T> = net.minecraft.server.v1_16_R3.DataWatcher.Item<T>

typealias NMS16DataWatcherRegistry = net.minecraft.server.v1_16_R3.DataWatcherRegistry

typealias NMS16IChatBaseComponent = net.minecraft.server.v1_16_R3.IChatBaseComponent

typealias NMS16IBlockAccess = net.minecraft.server.v1_16_R3.IBlockAccess

typealias NMS16Vec3D = net.minecraft.server.v1_16_R3.Vec3D

typealias NMS16ParticleParam = net.minecraft.server.v1_16_R3.ParticleParam

typealias NMS16VillagerData = net.minecraft.server.v1_16_R3.VillagerData

typealias NMS16MathHelper = net.minecraft.server.v1_16_R3.MathHelper

typealias NMS16ChatSerializer = net.minecraft.server.v1_16_R3.IChatBaseComponent.ChatSerializer

typealias NMS16ChunkCoordIntPair = net.minecraft.server.v1_16_R3.ChunkCoordIntPair

typealias NMS16WorldServer = net.minecraft.server.v1_16_R3.WorldServer

typealias NMS16EntityPlayer = net.minecraft.server.v1_16_R3.EntityPlayer

typealias CraftPlayer16 = org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer

typealias CraftWorld16 = org.bukkit.craftbukkit.v1_16_R3.CraftWorld

typealias CraftEntity16 = org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity

typealias CraftItemStack16 = org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack

typealias CraftMagicNumbers16 = org.bukkit.craftbukkit.v1_16_R3.util.CraftMagicNumbers

typealias CraftTropicalFishPattern16 = org.bukkit.craftbukkit.v1_16_R3.entity.CraftTropicalFish.CraftPattern

// 1.14

typealias NMS14DataWatcher = net.minecraft.server.v1_14_R1.DataWatcher

typealias NMS14PacketDataSerializer = net.minecraft.server.v1_14_R1.PacketDataSerializer

typealias NMS14PacketPlayOutScoreboardTeam = net.minecraft.server.v1_14_R1.PacketPlayOutScoreboardTeam

// 1.13

typealias NMS13PacketPlayOutBed = net.minecraft.server.v1_13_R2.PacketPlayOutBed

typealias NMS13PacketPlayOutEntityLook = net.minecraft.server.v1_13_R2.PacketPlayOutEntity.PacketPlayOutEntityLook

typealias NMS13PacketPlayOutRelEntityMove = net.minecraft.server.v1_13_R2.PacketPlayOutEntity.PacketPlayOutRelEntityMove

typealias NMS13PacketPlayOutRelEntityMoveLook = net.minecraft.server.v1_13_R2.PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook

typealias NMS13PacketPlayOutEntityVelocity = net.minecraft.server.v1_13_R2.PacketPlayOutEntityVelocity

typealias NMS13PacketPlayOutEntityHeadRotation = net.minecraft.server.v1_13_R2.PacketPlayOutEntityHeadRotation

typealias NMS13PacketPlayOutEntityEquipment = net.minecraft.server.v1_13_R2.PacketPlayOutEntityEquipment

typealias NMS13PacketPlayOutMount = net.minecraft.server.v1_13_R2.PacketPlayOutMount

typealias NMS13PacketPlayOutEntityMetadata = net.minecraft.server.v1_13_R2.PacketPlayOutEntityMetadata

typealias NMS13PacketPlayOutScoreboardTeam = net.minecraft.server.v1_13_R2.PacketPlayOutScoreboardTeam

typealias NMS13PacketDataSerializer = net.minecraft.server.v1_13_R2.PacketDataSerializer

typealias NMS13EnumItemSlot = net.minecraft.server.v1_13_R2.EnumItemSlot

typealias NMS13RegistryBlocks<T> = net.minecraft.server.v1_13_R2.RegistryBlocks<T>

typealias NMS13EntityTypes<T> = net.minecraft.server.v1_13_R2.EntityTypes<T>

typealias NMS13DataWatcherObject<T> = net.minecraft.server.v1_13_R2.DataWatcherObject<T>

typealias NMS13DataWatcher = net.minecraft.server.v1_13_R2.DataWatcher

typealias NMS13DataWatcherItem<T> = net.minecraft.server.v1_13_R2.DataWatcher.Item<T>

typealias NMS13DataWatcherRegistry = net.minecraft.server.v1_13_R2.DataWatcherRegistry

typealias NMS13IRegistry<T> = net.minecraft.server.v1_13_R2.IRegistry<T>

typealias NMS13Particle<T> = net.minecraft.server.v1_13_R2.Particle<T>

typealias NMS13Particles = net.minecraft.server.v1_13_R2.Particles

typealias NMS13MinecraftKey = net.minecraft.server.v1_13_R2.MinecraftKey

typealias NMS13ParticleParam = net.minecraft.server.v1_13_R2.ParticleParam

typealias NMS13Vector3f = net.minecraft.server.v1_13_R2.Vector3f

typealias NMS13BlockPosition = net.minecraft.server.v1_13_R2.BlockPosition

typealias CraftItemStack13 = org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack

typealias CraftMagicNumbers13 = org.bukkit.craftbukkit.v1_13_R2.util.CraftMagicNumbers

// 1.12

typealias NMS12DataWatcher = net.minecraft.server.v1_12_R1.DataWatcher

typealias NMS12DataWatcherItem<T> = net.minecraft.server.v1_12_R1.DataWatcher.Item<T>

typealias NMS12DataWatcherObject<T> = net.minecraft.server.v1_12_R1.DataWatcherObject<T>

typealias NMS12DataWatcherRegistry = net.minecraft.server.v1_12_R1.DataWatcherRegistry

typealias NMS12BlockPosition = net.minecraft.server.v1_12_R1.BlockPosition

typealias NMS12Vector3f = net.minecraft.server.v1_12_R1.Vector3f

typealias CraftItemStack12 = org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack

typealias CraftMagicNumbers12 = org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers

typealias CraftWorld12 = org.bukkit.craftbukkit.v1_12_R1.CraftWorld

// 1.11

typealias NMS11PacketPlayOutSpawnEntity = net.minecraft.server.v1_11_R1.PacketPlayOutSpawnEntity

typealias NMS11PacketPlayOutSpawnEntityLiving = net.minecraft.server.v1_11_R1.PacketPlayOutSpawnEntityLiving

typealias NMS11DataWatcher = net.minecraft.server.v1_11_R1.DataWatcher

typealias NMS11PacketDataSerializer = net.minecraft.server.v1_11_R1.PacketDataSerializer

typealias NMS11DataWatcherItem<T> = net.minecraft.server.v1_11_R1.DataWatcher.Item<T>

typealias NMS11DataWatcherObject<T> = net.minecraft.server.v1_11_R1.DataWatcherObject<T>

typealias NMS11DataWatcherRegistry = net.minecraft.server.v1_11_R1.DataWatcherRegistry

typealias NMS11EnumGameMode = net.minecraft.server.v1_11_R1.EnumGamemode

typealias NMS11PacketPlayOutPlayerInfo = net.minecraft.server.v1_11_R1.PacketPlayOutPlayerInfo

typealias NMS11PacketPlayOutPlayerInfoEnumPlayerInfoAction = net.minecraft.server.v1_11_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction

typealias NMS11BlockPosition = net.minecraft.server.v1_11_R1.BlockPosition

typealias CraftWorld11 = org.bukkit.craftbukkit.v1_11_R1.CraftWorld

// 1.9

typealias NMS9PacketPlayOutSpawnEntity = net.minecraft.server.v1_9_R2.PacketPlayOutSpawnEntity

typealias NMS9PacketPlayOutSpawnEntityLiving = net.minecraft.server.v1_9_R2.PacketPlayOutSpawnEntityLiving

typealias NMS9PacketPlayOutSpawnEntityPainting = net.minecraft.server.v1_9_R2.PacketPlayOutSpawnEntityPainting

typealias NMS9PacketPlayOutSpawnEntityPlayer = net.minecraft.server.v1_9_R2.PacketPlayOutNamedEntitySpawn

typealias NMS9PacketPlayOutPlayerInfo = net.minecraft.server.v1_9_R2.PacketPlayOutPlayerInfo

typealias NMS9PacketPlayOutPlayerInfoEnumPlayerInfoAction = net.minecraft.server.v1_9_R2.PacketPlayOutPlayerInfo.EnumPlayerInfoAction

typealias NMS9PacketPlayOutEntityTeleport = net.minecraft.server.v1_9_R2.PacketPlayOutEntityTeleport

typealias NMS9PacketPlayOutScoreboardTeam = net.minecraft.server.v1_9_R2.PacketPlayOutScoreboardTeam

typealias NMS9EnumDirection = net.minecraft.server.v1_9_R2.EnumDirection

typealias NMS9EnumGameMode = net.minecraft.server.v1_9_R2.WorldSettings.EnumGamemode

typealias NMS9DataWatcher = net.minecraft.server.v1_9_R2.DataWatcher

typealias NMS9DataWatcherItem<T> = net.minecraft.server.v1_9_R2.DataWatcher.Item<T>

typealias NMS9DataWatcherObject<T> = net.minecraft.server.v1_9_R2.DataWatcherObject<T>

typealias NMS9DataWatcherRegistry = net.minecraft.server.v1_9_R2.DataWatcherRegistry

typealias NMS9PacketDataSerializer = net.minecraft.server.v1_9_R2.PacketDataSerializer

typealias NMS9IBlockAccess = net.minecraft.server.v1_9_R2.IBlockAccess

typealias NMS9Chunk = net.minecraft.server.v1_9_R2.Chunk

typealias NMS9BlockPosition = net.minecraft.server.v1_9_R2.BlockPosition

typealias CraftItemStack9 = org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack

typealias CraftWorld9 = org.bukkit.craftbukkit.v1_9_R2.CraftWorld

typealias CraftEntity9 = org.bukkit.craftbukkit.v1_9_R2.entity.CraftEntity

fun <T> nullable(value: T?): Optional<T> = Optional.fromNullable(value)