package ink.ptms.adyeshach.api.nms

import com.google.common.base.Enums
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import ink.ptms.adyeshach.common.bukkit.BukkitDirection
import ink.ptms.adyeshach.common.bukkit.BukkitPaintings
import ink.ptms.adyeshach.common.bukkit.BukkitParticles
import ink.ptms.adyeshach.common.bukkit.BukkitPose
import ink.ptms.adyeshach.common.bukkit.data.VectorNull
import ink.ptms.adyeshach.common.entity.EntityTypes
import net.minecraft.server.v1_13_R2.PacketPlayOutBed
import net.minecraft.server.v1_16_R1.*
import net.minecraft.server.v1_8_R3.DataWatcher.WatchableObject
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity.PacketPlayOutRelEntityMove
import net.minecraft.server.v1_8_R3.WorldSettings
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftEntity
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftTropicalFish
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack
import org.bukkit.craftbukkit.v1_16_R1.util.CraftMagicNumbers
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.TropicalFish
import org.bukkit.entity.Villager
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import org.bukkit.util.EulerAngle
import org.bukkit.util.Vector
import taboolib.common.reflect.Reflex.Companion.getProperty
import taboolib.common.reflect.Reflex.Companion.invokeMethod
import taboolib.common.reflect.Reflex.Companion.setProperty
import taboolib.common.reflect.Reflex.Companion.unsafeInstance
import taboolib.module.nms.*
import taboolib.module.nms.MinecraftVersion
import java.util.*

/**
 * @author Arasple
 * @date 2020/8/3 21:51
 */
class NMSImpl : NMS() {

    val isUniversal = MinecraftVersion.isUniversal

    val majorLegacy = MinecraftVersion.majorLegacy

    val classPlayerInfoData = nmsClass("PacketPlayOutPlayerInfo\$PlayerInfoData")

    fun Player.sendPacketI(packet: Any, vararg fields: Pair<String, Any?>) {
        sendPacket(setFields(packet, *fields))
    }

    fun setFields(any: Any, vararg fields: Pair<String, Any?>): Any {
        fields.forEach { (key, value) ->
            if (value != null) {
                any.setProperty(key, value)
            }
        }
        return any
    }

    override fun spawnEntity(player: Player, entityType: EntityTypes, entityId: Int, uuid: UUID, location: Location) {
        if (isUniversal) {
            player.sendPacketI(
                PacketPlayOutSpawnEntity::class.java.unsafeInstance(),
                "id" to entityId,
                "uuid" to uuid,
                "x" to location.x,
                "y" to location.y,
                "z" to location.z,
                "xa" to 0,
                "ya" to 0,
                "za" to 0,
                "xRot" to (location.yaw * 256.0f / 360.0f).toInt().toByte(),
                "yRot" to (location.pitch * 256.0f / 360.0f).toInt().toByte(),
                "type" to getEntityTypeNMS(entityType),
                "data" to 0
            )
        } else {
            //protocol 47兼容
            if (majorLegacy == 10806) {
                player.sendPacketI(
                    PacketPlayOutSpawnEntity(),
                    "a" to entityId,
                    "b" to MathHelper.floor(location.x * 32.0),
                    "c" to MathHelper.floor(location.y * 32.0),
                    "d" to MathHelper.floor(location.z * 32.0),
                    "e" to 0,
                    "f" to 0,
                    "g" to 0,
                    "h" to (location.yaw * 256.0f / 360.0f).toInt().toByte(),
                    "i" to (location.pitch * 256.0f / 360.0f).toInt().toByte(),
                    "j" to entityType.bukkitId,
                    "k" to 0
                )
            } else {
                player.sendPacketI(
                    PacketPlayOutSpawnEntity(),
                    "a" to entityId,
                    "b" to uuid,
                    "c" to location.x,
                    "d" to location.y,
                    "e" to location.z,
                    "f" to (location.yaw * 256.0f / 360.0f).toInt().toByte(),
                    "g" to (location.pitch * 256.0f / 360.0f).toInt().toByte(),
                    "k" to if (majorLegacy <= 11300) entityType.bukkitId else getEntityTypeNMS(entityType)
                )
            }
        }
    }

    override fun spawnEntityLiving(player: Player, entityType: EntityTypes, entityId: Int, uuid: UUID, location: Location) {
        if (entityType == EntityTypes.ARMOR_STAND && majorLegacy < 11300) {
            return spawnEntity(player, entityType, entityId, uuid, location)
        }
        if (isUniversal) {
            player.sendPacketI(
                PacketPlayOutSpawnEntityLiving::class.java.unsafeInstance(),
                "id" to entityId,
                "uuid" to uuid,
                "type" to IRegistry.ENTITY_TYPE.invokeMethod<Int>("getId", getEntityTypeNMS(entityType))!!,
                "x" to location.x,
                "y" to location.y,
                "z" to location.z,
                "xd" to 0,
                "yd" to 0,
                "zd" to 0,
                "yRot" to (location.yaw * 256.0f / 360.0f).toInt().toByte(),
                "xRot" to (location.pitch * 256.0f / 360.0f).toInt().toByte(),
                "yHeadRot" to (location.yaw * 256.0f / 360.0f).toInt().toByte()
            )
        } else {
            if (majorLegacy == 10806) {
                player.sendPacketI(
                    PacketPlayOutSpawnEntityLiving(),
                    "a" to entityId,
                    "b" to entityType.bukkitId,
                    "c" to MathHelper.floor(location.x * 32.0),
                    "d" to MathHelper.floor(location.y * 32.0),
                    "e" to MathHelper.floor(location.z * 32.0),
                    "f" to 0,
                    "g" to 0,
                    "h" to 0,
                    "i" to (location.yaw * 256.0f / 360.0f).toInt().toByte(),
                    "j" to (location.pitch * 256.0f / 360.0f).toInt().toByte(),
                    "k" to (location.yaw * 256.0f / 360.0f).toInt().toByte(),
                    "l" to DataWatcher(null)
                )
            } else {
                player.sendPacketI(
                    PacketPlayOutSpawnEntityLiving(),
                    "a" to entityId,
                    "b" to uuid,
                    "c" to when {
                        majorLegacy >= 11400 -> IRegistry.ENTITY_TYPE.a(getEntityTypeNMS(entityType) as net.minecraft.server.v1_16_R1.EntityTypes<*>)
                        majorLegacy == 11300 -> net.minecraft.server.v1_13_R2.IRegistry.ENTITY_TYPE.a(getEntityTypeNMS(entityType) as net.minecraft.server.v1_13_R2.EntityTypes<*>)
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
                    "m" to if (majorLegacy >= 11500) null else DataWatcher(null)
                )
            }
        }
    }

    override fun spawnNamedEntity(player: Player, entityId: Int, uuid: UUID, location: Location) {
        if (isUniversal) {
            player.sendPacketI(
                PacketPlayOutNamedEntitySpawn::class.java.unsafeInstance(),
                "entityId" to entityId,
                "playerId" to uuid,
                "x" to location.x,
                "y" to location.y,
                "z" to location.z,
                "yRot" to (location.yaw * 256 / 360).toInt().toByte(),
                "xRot" to (location.pitch * 256 / 360).toInt().toByte()
            )
        } else {
            if (majorLegacy == 10806) {
                player.sendPacketI(
                    PacketPlayOutNamedEntitySpawn(),
                    "a" to entityId,
                    "b" to uuid,
                    "c" to MathHelper.floor(location.x * 32.0),
                    "d" to MathHelper.floor(location.y * 32.0),
                    "e" to MathHelper.floor(location.z * 32.0),
                    "f" to (location.yaw * 256 / 360).toInt().toByte(),
                    "g" to (location.pitch * 256 / 360).toInt().toByte(),
                    "h" to 0, //held item
                    "i" to DataWatcher(null)
                )
            } else {
                player.sendPacketI(
                    PacketPlayOutNamedEntitySpawn(),
                    "a" to entityId,
                    "b" to uuid,
                    "c" to location.x,
                    "d" to location.y,
                    "e" to location.z,
                    "f" to (location.yaw * 256 / 360).toInt().toByte(),
                    "g" to (location.pitch * 256 / 360).toInt().toByte(),
                    "h" to if (majorLegacy >= 11500) null else DataWatcher(null),
                )
            }
        }
    }

    override fun spawnEntityFallingBlock(player: Player, entityId: Int, uuid: UUID, location: Location, material: Material, data: Byte) {
        if (isUniversal) {
            val block = Blocks::class.java.getProperty<Any>(material.name, fixed = true)
            player.sendPacketI(
                PacketPlayOutSpawnEntity::class.java.unsafeInstance(),
                "id" to entityId,
                "uuid" to uuid,
                "x" to location.x,
                "y" to location.y,
                "z" to location.z,
                "xa" to 0,
                "ya" to 0,
                "za" to 0,
                "xRot" to (location.yaw * 256.0f / 360.0f).toInt().toByte(),
                "yRot" to (location.pitch * 256.0f / 360.0f).toInt().toByte(),
                "type" to getEntityTypeNMS(EntityTypes.FALLING_BLOCK),
                "data" to net.minecraft.world.level.block.Block.getId(((block ?: Blocks.STONE) as net.minecraft.world.level.block.Block).defaultBlockState())
            )
        } else if (majorLegacy >= 11300) {
            val block = Blocks::class.java.getProperty<Any>(material.name, fixed = true)
            player.sendPacketI(
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
            if (majorLegacy == 10806) {
                player.sendPacketI(
                    PacketPlayOutSpawnEntity(),
                    "a" to entityId,
                    "b" to MathHelper.floor(location.x * 32.0),
                    "c" to MathHelper.floor(location.y * 32.0),
                    "d" to MathHelper.floor(location.z * 32.0),
                    "e" to 0,
                    "f" to 0,
                    "g" to 0,
                    "h" to (location.pitch * 256.0f / 360.0f).toInt().toByte(),
                    "i" to (location.yaw * 256.0f / 360.0f).toInt().toByte(),
                    "j" to getEntityTypeNMS(EntityTypes.FALLING_BLOCK),
                    "k" to material.id + (data.toInt() shl 12)
                )
            } else {
                player.sendPacketI(
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
    }

    override fun spawnEntityExperienceOrb(player: Player, entityId: Int, location: Location, amount: Int) {
        if (isUniversal) {
            player.sendPacketI(
                PacketPlayOutSpawnEntityExperienceOrb::class.java.unsafeInstance(),
                "id" to entityId,
                "x" to location.x,
                "y" to location.y,
                "z" to location.z,
                "value" to amount,
            )
        } else {
            player.sendPacketI(
                PacketPlayOutSpawnEntityExperienceOrb(),
                "a" to entityId,
                "b" to location.x,
                "c" to location.y,
                "d" to location.z,
                "e" to amount,
            )
        }
    }

    override fun spawnEntityPainting(player: Player, entityId: Int, uuid: UUID, location: Location, direction: BukkitDirection, painting: BukkitPaintings) {
        if (isUniversal) {
            player.sendPacketI(
                PacketPlayOutSpawnEntityPainting::class.java.unsafeInstance(),
                "id" to entityId,
                "uuid" to uuid,
                "pos" to getBlockPositionNMS(location),
                "direction" to Enums.getIfPresent(EnumDirection::class.java, direction.name).get(),
                "motive" to IRegistry.MOTIVE.a(getPaintingNMS(painting) as Paintings?)
            )
        } else if (majorLegacy > 11300) {
            player.sendPacketI(
                PacketPlayOutSpawnEntityPainting(),
                "a" to entityId,
                "b" to uuid,
                "c" to getBlockPositionNMS(location),
                "d" to Enums.getIfPresent(EnumDirection::class.java, direction.name).get(),
                "e" to IRegistry.MOTIVE.a(getPaintingNMS(painting) as Paintings?)
            )
        } else {
            if (majorLegacy == 10806) {
                player.sendPacketI(
                    PacketPlayOutSpawnEntityPainting(),
                    "a" to entityId,
                    "b" to getBlockPositionNMS(location),
                    "c" to Enums.getIfPresent(net.minecraft.server.v1_9_R2.EnumDirection::class.java, direction.name).get(),
                    "d" to getPaintingNMS(painting)
                )
            } else {
                player.sendPacketI(
                    net.minecraft.server.v1_9_R2.PacketPlayOutSpawnEntityPainting(),
                    "a" to entityId,
                    "b" to uuid,
                    "c" to getBlockPositionNMS(location),
                    "d" to Enums.getIfPresent(net.minecraft.server.v1_9_R2.EnumDirection::class.java, direction.name).get(),
                    "e" to getPaintingNMS(painting)
                )
            }
        }
    }

    override fun addPlayerInfo(player: Player, uuid: UUID, name: String, ping: Int, texture: Array<String>) {
        val profile = GameProfile(uuid, name)
        if (texture.size == 2) {
            profile.properties.put("textures", Property("textures", texture[0], texture[1]))
        }
        val infoData = classPlayerInfoData.unsafeInstance()
        if (isUniversal) {
            val info = PacketPlayOutPlayerInfo::class.java.unsafeInstance()
            infoData.setProperty("a", ping)
            infoData.setProperty("b", EnumGamemode.values()[0])
            infoData.setProperty("c", profile)
            infoData.setProperty("d", craftChatMessageFromString(name))
            player.sendPacketI(
                info,
                "a" to PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,
                "b" to listOf(infoData)
            )
        } else if (majorLegacy >= 11000) {
            val info = PacketPlayOutPlayerInfo()
            infoData.setProperty("b", ping)
            infoData.setProperty("c", EnumGamemode.values()[0])
            infoData.setProperty("d", profile)
            infoData.setProperty("e", craftChatMessageFromString(name))
            player.sendPacketI(
                info,
                "a" to PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,
                "b" to listOf(infoData)
            )
        } else {
            val info = net.minecraft.server.v1_9_R2.PacketPlayOutPlayerInfo()
            infoData.setProperty("b", ping)
            if (majorLegacy == 10806) {
                infoData.setProperty("c", WorldSettings.EnumGamemode.values()[0])
            } else {
                infoData.setProperty("c", EnumGamemode.values()[0])
            }
            infoData.setProperty("d", profile)
            infoData.setProperty("e", craftChatMessageFromString(name))
            player.sendPacketI(
                info,
                "a" to PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,
                "b" to listOf(infoData)
            )
        }
    }

    override fun removePlayerInfo(player: Player, uuid: UUID) {
        val infoData = classPlayerInfoData.unsafeInstance()
        if (isUniversal) {
            val info = PacketPlayOutPlayerInfo::class.java.unsafeInstance()
            infoData.setProperty("a", -1)
            infoData.setProperty("c", GameProfile(uuid, ""))
            player.sendPacketI(
                info,
                "a" to PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,
                "b" to listOf(infoData)
            )
        } else {
            val info = PacketPlayOutPlayerInfo()
            infoData.setProperty("b", -1)
            infoData.setProperty("d", GameProfile(uuid, ""))
            player.sendPacketI(
                info,
                "a" to PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,
                "b" to listOf(infoData)
            )
        }
    }

    override fun destroyEntity(player: Player, entityId: Int) {
        player.sendPacketI(PacketPlayOutEntityDestroy(entityId))
    }

    override fun teleportEntity(player: Player, entityId: Int, location: Location) {
        if (isUniversal) {
            player.sendPacketI(
                PacketPlayOutEntityTeleport::class.java.unsafeInstance(),
                "id" to entityId,
                "x" to location.x,
                "y" to location.y,
                "z" to location.z,
                "yRot" to (location.yaw * 256 / 360).toInt().toByte(),
                "xRot" to (location.pitch * 256 / 360).toInt().toByte(),
                "onGround" to false
            )
        } else {
            player.sendPacketI(
                PacketPlayOutEntityTeleport(),
                "a" to entityId,
                "b" to if (majorLegacy == 10806) MathHelper.floor(location.x * 32.0) else location.x,
                "c" to if (majorLegacy == 10806) MathHelper.floor(location.y * 32.0) else location.y,
                "d" to if (majorLegacy == 10806) MathHelper.floor(location.z * 32.0) else location.z,
                "e" to (location.yaw * 256 / 360).toInt().toByte(),
                "f" to (location.pitch * 256 / 360).toInt().toByte(),
                "g" to false // onGround
            )
        }
    }

    override fun relMoveEntity(player: Player, entityId: Int, x: Double, y: Double, z: Double) {
        if (majorLegacy >= 11400) {
            player.sendPacketI(
                PacketPlayOutEntity.PacketPlayOutRelEntityMove(
                    entityId,
                    (x * 4096).toInt().toShort(),
                    (y * 4096).toInt().toShort(),
                    (z * 4096).toInt().toShort(),
                    true
                )
            )
        } else {
            if (majorLegacy == 10806) {
                player.sendPacketI(PacketPlayOutRelEntityMove(entityId, (x * 32).toInt().toByte(), (y * 32).toInt().toByte(), (z * 32).toInt().toByte(), true))
            } else {
                player.sendPacketI(net.minecraft.server.v1_13_R2.PacketPlayOutEntity.PacketPlayOutRelEntityMove(entityId, x.toLong(), y.toLong(), z.toLong(), true))
            }
        }
    }

    override fun updateEntityVelocity(player: Player, entityId: Int, vector: Vector) {
        if (majorLegacy >= 11400) {
            player.sendPacketI(PacketPlayOutEntityVelocity(entityId, Vec3D(vector.x, vector.y, vector.z)))
        } else {
            player.sendPacketI(net.minecraft.server.v1_12_R1.PacketPlayOutEntityVelocity(entityId, vector.x, vector.y, vector.z))
        }
    }

    override fun setHeadRotation(player: Player, entityId: Int, yaw: Float, pitch: Float) {
        if (isUniversal) {
            player.sendPacketI(
                PacketPlayOutEntityHeadRotation::class.java.unsafeInstance(),
                "entityId" to entityId,
                "yHeadRot" to MathHelper.d(yaw * 256.0f / 360.0f).toByte()
            )
        } else {
            player.sendPacketI(
                PacketPlayOutEntityHeadRotation(),
                "a" to entityId,
                "b" to MathHelper.d(yaw * 256.0f / 360.0f).toByte()
            )
        }
        player.sendPacketI(
            PacketPlayOutEntity.PacketPlayOutEntityLook(
                entityId,
                MathHelper.d(yaw * 256.0f / 360.0f).toByte(),
                MathHelper.d(pitch * 256.0f / 360.0f).toByte(),
                true
            )
        )
    }

    @Suppress("LiftReturnOrAssignment")
    override fun updateEquipment(player: Player, entityId: Int, slot: EquipmentSlot, itemStack: ItemStack) {
        val major = MinecraftVersion.major
        when {
            major >= 8 -> {
                val enumItemSlot: EnumItemSlot
                if (slot == EquipmentSlot.HAND) {
                    enumItemSlot = EnumItemSlot.MAINHAND
                } else if (slot == EquipmentSlot.OFF_HAND) {
                    enumItemSlot = EnumItemSlot.OFFHAND
                } else if (slot == EquipmentSlot.FEET) {
                    enumItemSlot = EnumItemSlot.FEET
                } else if (slot == EquipmentSlot.LEGS) {
                    enumItemSlot = EnumItemSlot.LEGS
                } else if (slot == EquipmentSlot.CHEST) {
                    enumItemSlot = EnumItemSlot.CHEST
                } else if (slot == EquipmentSlot.HEAD) {
                    enumItemSlot = EnumItemSlot.HEAD
                } else {
                    error("out of case")
                }
                player.sendPacketI(
                    PacketPlayOutEntityEquipment(entityId,
                        listOf(com.mojang.datafixers.util.Pair(enumItemSlot, CraftItemStack.asNMSCopy(itemStack)))
                    )
                )
            }
            major >= 1 -> {
                val enumItemSlot: net.minecraft.server.v1_13_R2.EnumItemSlot
                if (slot == EquipmentSlot.HAND) {
                    enumItemSlot = net.minecraft.server.v1_13_R2.EnumItemSlot.MAINHAND
                } else if (slot == EquipmentSlot.OFF_HAND) {
                    enumItemSlot = net.minecraft.server.v1_13_R2.EnumItemSlot.OFFHAND
                } else if (slot == EquipmentSlot.FEET) {
                    enumItemSlot = net.minecraft.server.v1_13_R2.EnumItemSlot.FEET
                } else if (slot == EquipmentSlot.LEGS) {
                    enumItemSlot = net.minecraft.server.v1_13_R2.EnumItemSlot.LEGS
                } else if (slot == EquipmentSlot.CHEST) {
                    enumItemSlot = net.minecraft.server.v1_13_R2.EnumItemSlot.CHEST
                } else if (slot == EquipmentSlot.HEAD) {
                    enumItemSlot = net.minecraft.server.v1_13_R2.EnumItemSlot.HEAD
                } else {
                    error("out of case")
                }
                player.sendPacketI(
                    net.minecraft.server.v1_13_R2.PacketPlayOutEntityEquipment(
                        entityId,
                        enumItemSlot,
                        org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack.asNMSCopy(itemStack)
                    )
                )
            }
            else -> {
                val nmsSlot =
                    if (slot == EquipmentSlot.HAND) {
                        0
                    } else if (slot == EquipmentSlot.FEET) {
                        1
                    } else if (slot == EquipmentSlot.LEGS) {
                        2
                    } else if (slot == EquipmentSlot.CHEST) {
                        3
                    } else if (slot == EquipmentSlot.HEAD) {
                        4
                    } else {
                        0
                    }

                player.sendPacketI(
                    net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment(
                        entityId,
                        nmsSlot,
                        org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack.asNMSCopy(itemStack)
                    )
                )
            }
        }
    }

    override fun updatePassengers(player: Player, entityId: Int, vararg passengers: Int) {
        if (isUniversal) {
            player.sendPacketI(
                PacketPlayOutMount::class.java.unsafeInstance(),
                "vehicle" to entityId,
                "passengers" to passengers
            )
        } else {
            if (majorLegacy != 10806) {
                player.sendPacketI(
                    PacketPlayOutMount(),
                    "a" to entityId,
                    "b" to passengers
                )
            }
        }
    }

    override fun updateEntityMetadata(player: Player, entityId: Int, vararg objects: Any) {
        if (isUniversal) {
            player.sendPacketI(
                PacketPlayOutEntityMetadata::class.java.unsafeInstance(),
                "id" to entityId,
                "packedItems" to objects.map { it as DataWatcher.Item<*> }.toList()
            )
        } else {
            if (majorLegacy == 10806) {
                player.sendPacketI(
                    PacketPlayOutEntityMetadata(),
                    "a" to entityId,
                    "b" to objects.map { it as WatchableObject }.toList()
                )
            } else {
                player.sendPacketI(
                    PacketPlayOutEntityMetadata(),
                    "a" to entityId,
                    "b" to objects.map { it as DataWatcher.Item<*> }.toList()
                )
            }
        }
    }

    override fun getMetaEntityInt(index: Int, value: Int): Any {
        return if (majorLegacy == 10806) {
            WatchableObject(2, index, value)
        } else {
            DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.b), value)
        }
    }

    override fun getMetaEntityFloat(index: Int, value: Float): Any {
        return if (majorLegacy == 10806) {
            WatchableObject(3, index, value)
        } else {
            DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.c), value)
        }
    }

    override fun getMetaEntityString(index: Int, value: String): Any {
        return if (majorLegacy == 10806) {
            WatchableObject(4, index, value)
        } else {
            DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.d), value)
        }
    }

    override fun getMetaEntityBoolean(index: Int, value: Boolean): Any {
        return if (MinecraftVersion.major >= 5) {
            DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.i), value)
        } else {
            if (majorLegacy == 10806) {
                WatchableObject(0, index, (if (value) 1 else 0).toByte())
            } else {
                net.minecraft.server.v1_11_R1.DataWatcher.Item(
                    net.minecraft.server.v1_11_R1.DataWatcherObject(
                        index,
                        net.minecraft.server.v1_11_R1.DataWatcherRegistry.h
                    ), value
                )
            }
        }
    }

    override fun getMetaEntityParticle(index: Int, value: BukkitParticles): Any {
        return if (majorLegacy == 10806) {
            WatchableObject(2, index, 0)
        } else {
            DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.j), getParticleNMS(value) as ParticleParam)
        }
    }

    override fun getMetaEntityByte(index: Int, value: Byte): Any {
        return if (majorLegacy == 10806) {
            WatchableObject(0, index, value)
        } else {
            DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.a), value)
        }
    }

    override fun getMetaEntityEulerAngle(index: Int, value: EulerAngle): Any {
        return if (MinecraftVersion.major >= 5) {
            DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.k), Vector3f(value.x.toFloat(), value.y.toFloat(), value.z.toFloat()))
        } else {
            if (majorLegacy == 10806) {
                WatchableObject(7, index, Vector3f(value.x.toFloat(), value.y.toFloat(), value.z.toFloat()))
            } else {
                net.minecraft.server.v1_12_R1.DataWatcher.Item(
                    net.minecraft.server.v1_12_R1.DataWatcherObject(
                        index,
                        net.minecraft.server.v1_12_R1.DataWatcherRegistry.i
                    ), net.minecraft.server.v1_12_R1.Vector3f(value.x.toFloat(), value.y.toFloat(), value.z.toFloat())
                )
            }
        }
    }

    override fun getMetaEntityVector(index: Int, value: Vector?): Any {
        return if (MinecraftVersion.major >= 5) {
            DataWatcher.Item(
                DataWatcherObject(index, DataWatcherRegistry.m),
                Optional.ofNullable(if (value == null || value is VectorNull) null else BlockPosition(value.x, value.y, value.z))
            )
        } else {
            if (majorLegacy == 10806) {
                WatchableObject(6, index, BlockPosition(value?.x ?: 0.0, value?.y ?: 0.0, value?.z ?: 0.0))
            } else {
                net.minecraft.server.v1_12_R1.DataWatcher.Item(
                    net.minecraft.server.v1_12_R1.DataWatcherObject(index, net.minecraft.server.v1_12_R1.DataWatcherRegistry.k),
                    com.google.common.base.Optional.fromNullable(
                        if (value == null || value is VectorNull) null else net.minecraft.server.v1_12_R1.BlockPosition(
                            value.x,
                            value.y,
                            value.z
                        )
                    )
                )
            }
        }
    }

    override fun getMetaEntityBlockData(index: Int, value: MaterialData?): Any {
        return if (MinecraftVersion.major >= 5) {
            DataWatcher.Item(
                DataWatcherObject(index, DataWatcherRegistry.h),
                Optional.ofNullable(if (value == null) null else CraftMagicNumbers.getBlock(value))
            )
        } else {
            if (majorLegacy == 10806) {
                val iBlockData = org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers.getBlock(value?.itemType ?: Material.AIR)
                        .fromLegacyData(value?.data?.toInt() ?: 0)

                WatchableObject(1, index, (net.minecraft.server.v1_12_R1.Block.getCombinedId(iBlockData) and '\uffff'.code).toShort())
            } else {
                net.minecraft.server.v1_12_R1.DataWatcher.Item(
                    net.minecraft.server.v1_12_R1.DataWatcherObject(
                        index,
                        net.minecraft.server.v1_12_R1.DataWatcherRegistry.g
                    ),
                    com.google.common.base.Optional.fromNullable(
                        if (value != null) {
                            org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers.getBlock(value.itemType)
                                .fromLegacyData(value.data.toInt())
                        } else {
                            null
                        }
                    )
                )
            }
        }
    }

    override fun getMetaEntityChatBaseComponent(index: Int, name: String?): Any {
        return if (MinecraftVersion.major >= 5) {
            DataWatcher.Item<Optional<IChatBaseComponent>>(
                DataWatcherObject(index, DataWatcherRegistry.f),
                Optional.ofNullable(if (name == null) null else (craftChatMessageFromString(name) as IChatBaseComponent))
            )
        } else {
            if (majorLegacy == 10806) {
                WatchableObject(4, index, name ?: "")
            } else {
                net.minecraft.server.v1_12_R1.DataWatcher.Item(
                    net.minecraft.server.v1_12_R1.DataWatcherObject(
                        index,
                        net.minecraft.server.v1_12_R1.DataWatcherRegistry.d
                    ), name ?: ""
                )
            }
        }
    }

    override fun getMetaItem(index: Int, itemStack: ItemStack): Any {
        val major = MinecraftVersion.major
        return when {
            major >= 5 -> {
                DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.g), CraftItemStack.asNMSCopy(itemStack))
            }
            major >= 4 -> {
                net.minecraft.server.v1_12_R1.DataWatcher.Item(
                    net.minecraft.server.v1_12_R1.DataWatcherObject(
                        6,
                        net.minecraft.server.v1_12_R1.DataWatcherRegistry.f
                    ), org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack.asNMSCopy(itemStack)
                )
            }
            major == 0 -> {
                WatchableObject(5, index, CraftItemStack.asNMSCopy(itemStack))
            }
            else -> {
                 net.minecraft.server.v1_9_R2.DataWatcher.Item(
                    net.minecraft.server.v1_9_R2.DataWatcherObject(
                        6,
                        net.minecraft.server.v1_9_R2.DataWatcherRegistry.f
                    ), com.google.common.base.Optional.fromNullable(org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack.asNMSCopy(itemStack))
                )
            }
        }
    }

    override fun getMetaVillagerData(index: Int, villagerData: ink.ptms.adyeshach.common.bukkit.data.VillagerData): Any {
        return DataWatcher.Item(
            DataWatcherObject(index, DataWatcherRegistry.q), VillagerData(
                when (villagerData.type) {
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
                }, 1
            )
        )
    }

    override fun getMetaEntityPose(index: Int, pose: BukkitPose): Any {
        return DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.s), Enums.getIfPresent(EntityPose::class.java, pose.name).or(EntityPose.STANDING))
    }

    override fun getEntityTypeNMS(entityTypes: EntityTypes): Any {
        return if (MinecraftVersion.major >= 5) {
            net.minecraft.server.v1_16_R1.EntityTypes::class.java.getProperty<Any>(entityTypes.internalName ?: entityTypes.name, fixed = true)!!
        } else {
            entityTypes.bukkitId
        }
    }

    override fun getBlockPositionNMS(location: Location): Any {
        return BlockPosition(location.blockX, location.blockY, location.blockZ)
    }

    override fun getPaintingNMS(bukkitPaintings: BukkitPaintings): Any {
        return if (MinecraftVersion.major >= 5) {
            Paintings::class.java.getProperty<Any>(bukkitPaintings.index.toString(), fixed = true)!!
        } else {
            bukkitPaintings.legacy
        }
    }

    override fun getParticleNMS(bukkitParticles: BukkitParticles): Any {
        return when {
            majorLegacy >= 11400 -> {
                Particles::class.java.getProperty<Any>(bukkitParticles.name, fixed = true) ?: Particles.FLAME
            }
            majorLegacy == 11300 -> {
                val p =
                    IRegistry.PARTICLE_TYPE.get(MinecraftKey(bukkitParticles.name.lowercase())) ?: net.minecraft.server.v1_13_R2.Particles.y
                if (p is net.minecraft.server.v1_13_R2.Particle<*>) {
                    p.f()
                } else {
                    p
                }
            }
            else -> 0
        }
    }

    override fun getEntityDataWatcher(entity: Entity): Any {
        return (entity as CraftEntity).handle.dataWatcher
    }

    override fun toBlockId(materialData: MaterialData): Int {
        return if (MinecraftVersion.major >= 10) {
            net.minecraft.world.level.block.Block::class.java.invokeMethod("getId", CraftMagicNumbers.getBlock(materialData), fixed = true)!!
        } else if (MinecraftVersion.major >= 5) {
            Block.getCombinedId(CraftMagicNumbers.getBlock(materialData))
        } else {
            materialData.itemType.id + (materialData.data.toInt() shl 12)
        }
    }

    override fun getEntity(world: World, id: Int): Entity? {
        return (world as CraftWorld).handle.getEntity(id)?.bukkitEntity
    }

    override fun parseVec3d(obj: Any): Vector {
        return if (MinecraftVersion.major == 0) {
            Vector((obj as net.minecraft.server.v1_8_R3.Vec3D).a, obj.b, obj.c)
        } else {
            Vector((obj as Vec3D).x, obj.y, obj.z)
        }
    }

    override fun getBlockHeight(block: org.bukkit.block.Block): Double {
        return if (MinecraftVersion.major >= 5) {
            if (block.type.isSolid) {
                (block.boundingBox.maxY - block.y).coerceAtLeast(0.0)
            } else {
                0.0
            }
        } else {
            when {
                majorLegacy > 11200 -> {
                    val p = net.minecraft.server.v1_12_R1.BlockPosition(block.x, block.y, block.z)
                    val b = (block.world as org.bukkit.craftbukkit.v1_12_R1.CraftWorld).handle.getType(p)
                    if (block.type.isSolid) {
                        val a = b.d((block.world as org.bukkit.craftbukkit.v1_12_R1.CraftWorld).handle, p)
                        a?.e ?: 0.0
                    } else {
                        0.0
                    }
                }
                majorLegacy > 11100 -> {
                    val p = net.minecraft.server.v1_11_R1.BlockPosition(block.x, block.y, block.z)
                    val b = (block.world as org.bukkit.craftbukkit.v1_11_R1.CraftWorld).handle.getType(p)
                    if (block.type.isSolid) {
                        val a = b.c((block.world as org.bukkit.craftbukkit.v1_11_R1.CraftWorld).handle, p)
                        a?.e ?: 0.0
                    } else {
                        0.0
                    }
                }
                else -> {
                    if (block.isEmpty) {
                        0.0
                    } else {
                        if (block.type.isSolid) {
                            if (MinecraftVersion.major == 0) {
                                val p = net.minecraft.server.v1_8_R3.BlockPosition(block.x, block.y, block.z)
                                val b = (block.world as org.bukkit.craftbukkit.v1_8_R3.CraftWorld).handle.getType(p)
                                b.block.a((block.world as org.bukkit.craftbukkit.v1_8_R3.CraftWorld).handle,p,b.block.blockData)?.e ?: 0.0
                            } else {
                                val p = net.minecraft.server.v1_9_R2.BlockPosition(block.x, block.y, block.z)
                                val b = (block.world as org.bukkit.craftbukkit.v1_9_R2.CraftWorld).handle.getType(p)
                                b.c((block.world as org.bukkit.craftbukkit.v1_9_R2.CraftWorld).handle, p)?.e ?: 0.0
                            }
                        } else {
                            0.0
                        }
                    }
                }
            }
        }
    }

    override fun sendAnimation(player: Player, id: Int, type: Int) {
        if (isUniversal) {
            player.sendPacketI(PacketPlayOutAnimation::class.java.unsafeInstance(), "id" to id, "action" to type)
        } else {
            player.sendPacketI(PacketPlayOutAnimation(), "a" to id, "b" to type)
        }
    }

    override fun sendAttachEntity(player: Player, attached: Int, holding: Int) {
        if (isUniversal) {
            player.sendPacketI(PacketPlayOutAttachEntity::class.java.unsafeInstance(), "sourceId" to attached, "destId" to holding)
        } else {
            if (majorLegacy == 10806) {
                player.sendPacketI(
                    PacketPlayOutAttachEntity(),
                    "a" to 1, //If true leashes the entity to the vehicle
                    "b" to attached,
                    "c" to holding
                )
            } else {
                player.sendPacketI(PacketPlayOutAttachEntity(), "a" to attached, "b" to holding)
            }
        }
    }

    override fun sendPlayerSleeping(player: Player, id: Int, location: Location) {
        player.sendPacketI(PacketPlayOutBed(), "a" to id, "b" to net.minecraft.server.v1_13_R2.BlockPosition(location.blockX, location.blockY, location.blockZ))
    }

    override fun getTropicalFishPattern(data: Int): TropicalFish.Pattern {
        return CraftTropicalFish.CraftPattern.fromData(data and '\uffff'.code)
    }

    override fun getTropicalFishDataValue(pattern: TropicalFish.Pattern): Int {
        return CraftTropicalFish.CraftPattern.values()[pattern.ordinal].dataValue
    }

    private fun craftChatMessageFromString(message: String): Any? {
        return obcClass("util.CraftChatMessage").invokeMethod<Array<*>>("fromString", message, fixed = true)!![0]
    }
}