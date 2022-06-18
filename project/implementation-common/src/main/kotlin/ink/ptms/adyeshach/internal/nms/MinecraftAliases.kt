package ink.ptms.adyeshach.internal

import com.google.common.base.Optional

// Universal

typealias NMSPacketOutSpawnEntity = net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity

typealias NMSPacketPlayOutSpawnEntityPlayer = net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn

typealias NMSPacketPlayOutSpawnEntityExperienceOrb = net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityExperienceOrb

typealias NMSPacketPlayOutPlayerInfo = net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo

typealias NMSPacketPlayOutPlayerInfoEnumPlayerInfoAction = net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo.EnumPlayerInfoAction

typealias NMSPacketPlayOutEntityDestroy = net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy

typealias NMSPacketPlayOutEntityTeleport = net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport

typealias NMSPacketPlayOutRelEntityMove = net.minecraft.network.protocol.game.PacketPlayOutEntity.PacketPlayOutRelEntityMove

typealias NMSPacketPlayOutEntityVelocity = net.minecraft.network.protocol.game.PacketPlayOutEntityVelocity

typealias NMSPacketPlayOutEntityHeadRotation = net.minecraft.network.protocol.game.PacketPlayOutEntityHeadRotation

typealias NMSPacketPlayOutEntityLook = net.minecraft.network.protocol.game.PacketPlayOutEntity.PacketPlayOutEntityLook

typealias NMSPacketPlayOutEntityEquipment = net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment

typealias NMSPacketPlayOutMount = net.minecraft.network.protocol.game.PacketPlayOutMount

typealias NMSPacketPlayOutEntityMetadata = net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata

typealias NMSPacketPlayOutAnimation = net.minecraft.network.protocol.game.PacketPlayOutAnimation

typealias NMSPacketPlayOutAttachEntity = net.minecraft.network.protocol.game.PacketPlayOutAttachEntity

typealias NMSBlock = net.minecraft.world.level.block.Block

typealias NMSEnumDirection = net.minecraft.core.EnumDirection

typealias NMSEnumGameMode = net.minecraft.world.level.EnumGamemode

typealias NMSEnumItemSlot = net.minecraft.world.entity.EnumItemSlot

typealias NMSBlockPosition = net.minecraft.core.Position

typealias NMSEntityPose = net.minecraft.world.entity.EntityPose

typealias NMSVector3f = net.minecraft.core.Vector3f

typealias NMSVillagerType = net.minecraft.world.entity.npc.VillagerType

typealias NMSVillagerProfession = net.minecraft.world.entity.npc.VillagerProfession

typealias NMSVillagerData = net.minecraft.world.entity.npc.VillagerData

typealias NMSDataWatcherObject<T> = net.minecraft.network.syncher.DataWatcherObject<T>

typealias NMSDataWatcher = net.minecraft.network.syncher.DataWatcher

typealias NMSDataWatcherItem<T> = net.minecraft.network.syncher.DataWatcher.Item<T>

typealias NMSDataWatcherRegistry = net.minecraft.network.syncher.DataWatcherRegistry

typealias NMSIChatBaseComponent = net.minecraft.network.chat.IChatBaseComponent

typealias CraftItemStack19 = org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack

typealias CraftMagicNumbers19 = org.bukkit.craftbukkit.v1_19_R1.util.CraftMagicNumbers

typealias CraftTropicalFishPattern19 = org.bukkit.craftbukkit.v1_19_R1.entity.CraftTropicalFish.CraftPattern

// 1.16

typealias NMS16PacketOutSpawnEntity = net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntity

typealias NMS16PacketPlayOutSpawnEntityPlayer = net.minecraft.server.v1_16_R3.PacketPlayOutNamedEntitySpawn

typealias NMS16PacketPlayOutSpawnEntityLiving = net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntityLiving

typealias NMS16PacketPlayOutSpawnEntityExperienceOrb = net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntityExperienceOrb

typealias NMS16PacketPlayOutPlayerInfo = net.minecraft.server.v1_16_R1.PacketPlayOutPlayerInfo

typealias NMS16PacketPlayOutPlayerInfoEnumPlayerInfoAction = net.minecraft.server.v1_16_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction

typealias NMS16PacketPlayOutEntityDestroy = net.minecraft.server.v1_16_R1.PacketPlayOutEntityDestroy

typealias NMS16PacketPlayOutEntityTeleport = net.minecraft.server.v1_16_R1.PacketPlayOutEntityTeleport

typealias NMS16PacketPlayOutRelEntityMove = net.minecraft.server.v1_16_R3.PacketPlayOutEntity.PacketPlayOutRelEntityMove

typealias NMS16PacketPlayOutEntityVelocity = net.minecraft.server.v1_16_R3.PacketPlayOutEntityVelocity

typealias NMS16PacketPlayOutEntityHeadRotation = net.minecraft.server.v1_16_R3.PacketPlayOutEntityHeadRotation

typealias NMS16PacketPlayOutEntityLook = net.minecraft.server.v1_16_R3.PacketPlayOutEntity.PacketPlayOutEntityLook

typealias NMS16PacketPlayOutEntityEquipment = net.minecraft.server.v1_16_R1.PacketPlayOutEntityEquipment

typealias NMS16PacketPlayOutMount = net.minecraft.server.v1_16_R3.PacketPlayOutMount

typealias NMS16PacketPlayOutEntityMetadata = net.minecraft.server.v1_16_R3.PacketPlayOutEntityMetadata

typealias NMS16PacketPlayOutAnimation = net.minecraft.server.v1_16_R3.PacketPlayOutAnimation

typealias NMS16PacketPlayOutAttachEntity = net.minecraft.server.v1_16_R3.PacketPlayOutAttachEntity

typealias NMS16EntityTypes<T> = net.minecraft.server.v1_16_R3.EntityTypes<T>

typealias NMS16IRegistry<T> = net.minecraft.server.v1_16_R3.IRegistry<T>

typealias NMS16Block = net.minecraft.server.v1_16_R3.Block

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

typealias CraftItemStack16 = org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack

typealias CraftMagicNumbers16 = org.bukkit.craftbukkit.v1_16_R3.util.CraftMagicNumbers

typealias CraftTropicalFishPattern16 = org.bukkit.craftbukkit.v1_16_R3.entity.CraftTropicalFish.CraftPattern

// 1.13

typealias NMS13PacketPlayOutBed = net.minecraft.server.v1_13_R2.PacketPlayOutBed

typealias NMS13PacketPlayOutRelEntityMove = net.minecraft.server.v1_13_R2.PacketPlayOutEntity.PacketPlayOutRelEntityMove

typealias NMS13PacketPlayOutEntityVelocity = net.minecraft.server.v1_13_R2.PacketPlayOutEntityVelocity

typealias NMS13PacketPlayOutEntityHeadRotation = net.minecraft.server.v1_13_R2.PacketPlayOutEntityHeadRotation

typealias NMS13PacketPlayOutEntityEquipment = net.minecraft.server.v1_13_R2.PacketPlayOutEntityEquipment

typealias NMS13PacketPlayOutMount = net.minecraft.server.v1_13_R2.PacketPlayOutMount

typealias NMS13PacketPlayOutEntityMetadata = net.minecraft.server.v1_13_R2.PacketPlayOutEntityMetadata

typealias NMS13EnumItemSlot = net.minecraft.server.v1_13_R2.EnumItemSlot

typealias NMS13EntityTypes<T> = net.minecraft.server.v1_13_R2.EntityTypes<T>

typealias NMS13IRegistry<T> = net.minecraft.server.v1_13_R2.IRegistry<T>

typealias CraftItemStack13 = org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack

typealias CraftMagicNumbers13 = org.bukkit.craftbukkit.v1_13_R2.util.CraftMagicNumbers

// 1.12

typealias NMS12DataWatcher = net.minecraft.server.v1_12_R1.DataWatcher

typealias NMS12DataWatcherObject<T> = net.minecraft.server.v1_12_R1.DataWatcherObject<T>

typealias NMS12DataWatcherRegistry = net.minecraft.server.v1_12_R1.DataWatcherRegistry

typealias CraftMagicNumbers12 = org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers

// 1.11

typealias NMS11DataWatcher = net.minecraft.server.v1_11_R1.DataWatcher

typealias NMS11DataWatcherObject<T> = net.minecraft.server.v1_11_R1.DataWatcherObject<T>

typealias NMS11DataWatcherRegistry = net.minecraft.server.v1_11_R1.DataWatcherRegistry

// 1.9

typealias NMS9PacketPlayOutSpawnEntityPainting = net.minecraft.server.v1_9_R2.PacketPlayOutSpawnEntityPainting

typealias NMS9PacketPlayOutPlayerInfo = net.minecraft.server.v1_9_R2.PacketPlayOutPlayerInfo

typealias NMS9PacketPlayOutPlayerInfoEnumPlayerInfoAction = net.minecraft.server.v1_9_R2.PacketPlayOutPlayerInfo.EnumPlayerInfoAction

typealias NMS9EnumDirection = net.minecraft.server.v1_9_R2.EnumDirection

typealias NMS9EnumGameMode = net.minecraft.server.v1_9_R2.WorldSettings.EnumGamemode

typealias CraftItemStack9 = org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack

fun <T> nullable(value: T?): Optional<T> = Optional.fromNullable(value)