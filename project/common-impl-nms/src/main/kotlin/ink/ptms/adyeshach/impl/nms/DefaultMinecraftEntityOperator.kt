package ink.ptms.adyeshach.impl.nms

import com.mojang.datafixers.util.Pair
import ink.ptms.adyeshach.api.dataserializer.createDataSerializer
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.MinecraftEntityOperator
import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftPacketHandler
import ink.ptms.adyeshach.core.bukkit.BukkitAnimation
import ink.ptms.adyeshach.core.util.ifloor
import ink.ptms.adyeshach.impl.nmsj17.NMSJ17
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.effect.MobEffectList
import net.minecraft.world.effect.MobEffects
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector
import taboolib.common.platform.function.info
import taboolib.module.nms.MinecraftVersion

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.DefaultMinecraftEntityOperator
 *
 * @author 坏黑
 * @since 2022/6/28 00:08
 */
class DefaultMinecraftEntityOperator : MinecraftEntityOperator {

    val isUniversal = MinecraftVersion.isUniversal
    val major = MinecraftVersion.major
    val majorLegacy = MinecraftVersion.majorLegacy

    val packetHandler: MinecraftPacketHandler
        get() = Adyeshach.api().getMinecraftAPI().getPacketHandler()

    override fun destroyEntity(player: List<Player>, entityId: Int) {
        packetHandler.sendPacket(player, NMSPacketPlayOutEntityDestroy(entityId))
    }

    override fun teleportEntity(player: List<Player>, entityId: Int, location: Location, onGround: Boolean) {
        // 修复视角
        val yf = Adyeshach.api().getEntityFinder().getEntityFromClientEntityId(entityId, player.firstOrNull() ?: return)?.entityType.fixYaw(location.yaw)
        // 计算视角
        val yaw = (yf * 256 / 360).toInt().toByte()
        val pitch = (location.pitch * 256 / 360).toInt().toByte()
        // 版本判断
        val packet: Any = when (major) {
            // 1.9, 1.10, 1.11, 1.12, 1.13, 1.14, 1.15, 1.16
            in 1..8 -> NMS9PacketPlayOutEntityTeleport().also {
                it.a(createDataSerializer {
                    writeVarInt(entityId)
                    writeDouble(location.x)
                    writeDouble(location.y)
                    writeDouble(location.z)
                    // 在传送包下，yaw 与 pitch的读取顺序颠倒
                    writeByte(yaw)
                    writeByte(pitch)
                    writeBoolean(onGround)
                }.toNMS() as NMS9PacketDataSerializer)
            }
            // 1.17, 1.18, 1.19, 1.12
            // 使用带有 DataSerializer 的构造函数生成数据包
            9, 10, 11, 12 -> NMSPacketPlayOutEntityTeleport(createDataSerializer {
                writeVarInt(entityId)
                writeDouble(location.x)
                writeDouble(location.y)
                writeDouble(location.z)
                writeByte(yaw)
                writeByte(pitch)
                writeBoolean(onGround)
            }.toNMS() as NMSPacketDataSerializer)
            // 不支持
            else -> error("Unsupported version.")
        }
        // 发送数据包
        packetHandler.sendPacket(player, packet)
    }

    override fun updateEntityLook(player: List<Player>, entityId: Int, yaw: Float, pitch: Float, onGround: Boolean) {
        // 修复视角
        val yf = Adyeshach.api().getEntityFinder().getEntityFromClientEntityId(entityId, player.firstOrNull() ?: return)?.entityType.fixYaw(yaw)
        // 计算视角
        val y = (yf * 256 / 360).toInt().toByte()
        val p = (pitch * 256 / 360).toInt().toByte()
        if (majorLegacy >= 11400) {
            packetHandler.sendPacket(player, NMSPacketPlayOutEntityLook(entityId, y, p, onGround))
        } else {
            packetHandler.sendPacket(player, NMS13PacketPlayOutEntityLook(entityId, y, p, onGround))
        }
        updateHeadRotation(player, entityId, yaw)
    }

    override fun updateRelEntityMove(player: List<Player>, entityId: Int, x: Short, y: Short, z: Short, onGround: Boolean) {
        if (majorLegacy >= 11400) {
            packetHandler.sendPacket(player, NMSPacketPlayOutRelEntityMove(entityId, x, y, z, onGround))
        } else {
            packetHandler.sendPacket(player, NMS13PacketPlayOutRelEntityMove(entityId, x.toLong(), y.toLong(), z.toLong(), onGround))
        }
    }

    override fun updateRelEntityMoveLook(
        player: List<Player>,
        entityId: Int,
        x: Short,
        y: Short,
        z: Short,
        yaw: Float,
        pitch: Float,
        onGround: Boolean,
    ) {
        // 修复视角
        val yf = Adyeshach.api().getEntityFinder().getEntityFromClientEntityId(entityId, player.firstOrNull() ?: return)?.entityType.fixYaw(yaw)
        // 计算视角
        val yRot = (yf * 256 / 360).toInt().toByte()
        val xRot = (pitch * 256 / 360).toInt().toByte()
        // 版本判断
        if (majorLegacy >= 11400) {
            packetHandler.sendPacket(player, NMSPacketPlayOutRelEntityMoveLook(entityId, x, y, z, yRot, xRot, onGround))
        } else {
            packetHandler.sendPacket(player, NMS13PacketPlayOutRelEntityMoveLook(entityId, x.toLong(), y.toLong(), z.toLong(), yRot, xRot, onGround))
        }
        updateHeadRotation(player, entityId, yaw)
    }

    override fun updateEntityVelocity(player: List<Player>, entityId: Int, vector: Vector) {
        if (majorLegacy >= 11400) {
            packetHandler.sendPacket(player, NMSPacketPlayOutEntityVelocity(entityId, NMSVec3D(vector.x, vector.y, vector.z)))
        } else {
            packetHandler.sendPacket(player, NMS13PacketPlayOutEntityVelocity(entityId, vector.x, vector.y, vector.z))
        }
    }

    override fun updateHeadRotation(player: List<Player>, entityId: Int, yaw: Float) {
        // 修复视角
        val yf = Adyeshach.api().getEntityFinder().getEntityFromClientEntityId(entityId, player.firstOrNull() ?: return)?.entityType.fixYaw(yaw)
        if (isUniversal) {
            packetHandler.sendPacket(player, NMSPacketPlayOutEntityHeadRotation(createDataSerializer {
                writeVarInt(entityId)
                writeByte(ifloor(yf * 256.0 / 360.0).toByte())
            }.toNMS() as NMSPacketDataSerializer))
        } else {
            packetHandler.sendPacket(player, NMS16PacketPlayOutEntityHeadRotation().also {
                it.a(createDataSerializer {
                    writeVarInt(entityId)
                    writeByte(ifloor(yf * 256.0 / 360.0).toByte())
                }.toNMS() as NMS16PacketDataSerializer)
            })
        }
    }

    override fun updateEquipment(player: List<Player>, entityId: Int, slot: EquipmentSlot, itemStack: ItemStack) {
        updateEquipment(player, entityId, mapOf(slot to itemStack))
    }

    override fun updateEquipment(player: List<Player>, entityId: Int, equipment: Map<EquipmentSlot, ItemStack>) {
        when {
            // 从 1.16 开始每个包支持多个物品
            majorLegacy >= 11600 -> {
                val items = equipment.map { Pair(it.key.toNMSEnumItemSlot(), CraftItemStack19.asNMSCopy(it.value)) }
                packetHandler.sendPacket(player, NMSPacketPlayOutEntityEquipment(entityId, items))
            }
            // 低版本
            else -> {
                equipment.forEach { (k, v) ->
                    packetHandler.sendPacket(player, NMS13PacketPlayOutEntityEquipment(entityId, k.toNMS13EnumItemSlot(), CraftItemStack13.asNMSCopy(v)))
                }
            }
        }
    }

    override fun updatePassengers(player: List<Player>, entityId: Int, vararg passengers: Int) {
        if (isUniversal) {
            packetHandler.sendPacket(player, NMSPacketPlayOutMount(createDataSerializer {
                writeVarInt(entityId)
                writeVarIntArray(passengers)
            }.toNMS() as NMSPacketDataSerializer))
        } else {
            packetHandler.sendPacket(player, NMS16PacketPlayOutMount().also {
                it.a(createDataSerializer {
                    writeVarInt(entityId)
                    writeVarIntArray(passengers)
                }.toNMS() as NMS16PacketDataSerializer)
            })
        }
    }

    override fun updateEntityMetadata(player: List<Player>, entityId: Int, metadata: List<MinecraftMeta>) {
        // 1.19.3 变更为 record 类型，因此无法兼容之前的写法
        if (majorLegacy >= 11903) {
            packetHandler.sendPacket(player, NMSJ17.instance.createPacketPlayOutEntityMetadata(entityId, metadata))
        } else if (isUniversal) {
            packetHandler.sendPacket(player, NMSPacketPlayOutEntityMetadata(createDataSerializer {
                writeVarInt(entityId)
                writeMetadata(metadata)
            }.toNMS() as NMSPacketDataSerializer))
        } else {
            packetHandler.sendPacket(player, NMS16PacketPlayOutEntityMetadata().also {
                it.a(createDataSerializer {
                    writeVarInt(entityId)
                    writeMetadata(metadata)
                }.toNMS() as NMS16PacketDataSerializer)
            })
        }
    }

    override fun updateEntityAnimation(player: List<Player>, entityId: Int, animation: BukkitAnimation) {
        if (isUniversal) {
            packetHandler.sendPacket(player, NMSPacketPlayOutAnimation(createDataSerializer {
                writeVarInt(entityId)
                writeByte(animation.ordinal.toByte())
            }.toNMS() as NMSPacketDataSerializer))
        } else {
            packetHandler.sendPacket(player, NMS16PacketPlayOutAnimation().also {
                it.a(createDataSerializer {
                    writeVarInt(entityId)
                    writeByte(animation.ordinal.toByte())
                }.toNMS() as NMS16PacketDataSerializer)
            })
        }
    }

    override fun updateEntityAttach(player: List<Player>, attached: Int, holding: Int) {
        if (isUniversal) {
            packetHandler.sendPacket(player, NMSPacketPlayOutAttachEntity(createDataSerializer {
                writeVarInt(attached)
                writeVarInt(holding)
            }.toNMS() as NMSPacketDataSerializer))
        } else {
            packetHandler.sendPacket(player, NMS16PacketPlayOutAttachEntity().also {
                it.a(createDataSerializer {
                    writeVarInt(attached)
                    writeVarInt(holding)
                }.toNMS() as NMS16PacketDataSerializer)
            })
        }
    }

    override fun updatePlayerSleeping(player: List<Player>, entityId: Int, location: Location) {
        packetHandler.sendPacket(player, NMS13PacketPlayOutBed().also {
            it.a(createDataSerializer {
                writeVarInt(entityId)
                writeBlockPosition(location.blockX, location.blockY, location.blockZ)
            }.toNMS() as NMS13PacketDataSerializer)
        })
    }

    fun EquipmentSlot.toNMSEnumItemSlot(): NMSEnumItemSlot {
        return when (this) {
            EquipmentSlot.HAND -> NMSEnumItemSlot.MAINHAND
            EquipmentSlot.OFF_HAND -> NMSEnumItemSlot.OFFHAND
            EquipmentSlot.FEET -> NMSEnumItemSlot.FEET
            EquipmentSlot.LEGS -> NMSEnumItemSlot.LEGS
            EquipmentSlot.CHEST -> NMSEnumItemSlot.CHEST
            EquipmentSlot.HEAD -> NMSEnumItemSlot.HEAD
            else -> error("Unknown EquipmentSlot: $this")
        }
    }

    fun EquipmentSlot.toNMS13EnumItemSlot(): NMS13EnumItemSlot {
        return when (this) {
            EquipmentSlot.HAND -> NMS13EnumItemSlot.MAINHAND
            EquipmentSlot.OFF_HAND -> NMS13EnumItemSlot.OFFHAND
            EquipmentSlot.FEET -> NMS13EnumItemSlot.FEET
            EquipmentSlot.LEGS -> NMS13EnumItemSlot.LEGS
            EquipmentSlot.CHEST -> NMS13EnumItemSlot.CHEST
            EquipmentSlot.HEAD -> NMS13EnumItemSlot.HEAD
            else -> error("Unknown EquipmentSlot: $this")
        }
    }
}