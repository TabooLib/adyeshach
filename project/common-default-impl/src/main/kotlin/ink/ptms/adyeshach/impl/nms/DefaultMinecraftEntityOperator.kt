package ink.ptms.adyeshach.impl.nms

import com.mojang.datafixers.util.Pair
import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.api.MinecraftEntityOperator
import ink.ptms.adyeshach.common.api.MinecraftMeta
import ink.ptms.adyeshach.common.api.MinecraftPacketHandler
import ink.ptms.adyeshach.common.bukkit.BukkitAnimation
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import taboolib.common.reflect.Reflex.Companion.unsafeInstance
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

    val majorLegacy = MinecraftVersion.majorLegacy

    val packetHandler: MinecraftPacketHandler
        get() = Adyeshach.api().getMinecraftAPI().getPacketHandler()

    override fun destroyEntity(player: Player, entityId: Int) {
        packetHandler.sendPacket(player, NMSPacketPlayOutEntityDestroy(entityId))
    }

    override fun teleportEntity(player: Player, entityId: Int, location: Location) {
        val packet = NMSPacketPlayOutEntityTeleport::class.java.unsafeInstance()
        val yRot = (location.yaw * 256 / 360).toInt().toByte()
        val xRot = (location.pitch * 256 / 360).toInt().toByte()
        if (isUniversal) {
            packetHandler.sendPacket(
                player,
                packet,
                "id" to entityId,
                "x" to location.x,
                "y" to location.y,
                "z" to location.z,
                "yRot" to yRot,
                "xRot" to xRot,
                "onGround" to false
            )
        } else {
            packetHandler.sendPacket(
                player,
                packet,
                "a" to entityId,
                "b" to location.x,
                "c" to location.y,
                "d" to location.z,
                "e" to yRot,
                "f" to xRot,
                "g" to false // onGround
            )
        }
    }

    override fun relMoveEntity(player: Player, entityId: Int, x: Double, y: Double, z: Double, onGround: Boolean) {
        if (majorLegacy >= 11400) {
            packetHandler.sendPacket(
                player,
                NMSPacketPlayOutRelEntityMove(
                    entityId,
                    (x * 4096).toInt().toShort(),
                    (y * 4096).toInt().toShort(),
                    (z * 4096).toInt().toShort(),
                    onGround
                )
            )
        } else {
            packetHandler.sendPacket(player, NMS13PacketPlayOutRelEntityMove(entityId, x.toLong(), y.toLong(), z.toLong(), onGround))
        }
    }

    override fun updateEntityVelocity(player: Player, entityId: Int, vector: Vector) {
        if (majorLegacy >= 11400) {
            packetHandler.sendPacket(
                player,
                NMSPacketPlayOutEntityVelocity(entityId, NMSVec3D(vector.x, vector.y, vector.z))
            )
        } else {
            packetHandler.sendPacket(
                player,
                NMS13PacketPlayOutEntityVelocity(entityId, vector.x, vector.y, vector.z)
            )
        }
    }

    override fun updateHeadRotation(player: Player, entityId: Int, yaw: Float, pitch: Float) {
        if (isUniversal) {
            val yRot = NMSMathHelper.floor(yaw * 256.0f / 360.0f).toByte()
            val xRot = NMSMathHelper.floor(pitch * 256.0f / 360.0f).toByte()
            packetHandler.sendPacket(player, NMSPacketPlayOutEntityLook(entityId, yRot, xRot, true))
            packetHandler.sendPacket(
                player,
                NMSPacketPlayOutEntityHeadRotation::class.java.unsafeInstance(),
                "entityId" to entityId,
                "yHeadRot" to yRot
            )
        } else {
            val yRot = NMS16MathHelper.d(yaw * 256.0f / 360.0f).toByte()
            val xRot = NMS16MathHelper.d(pitch * 256.0f / 360.0f).toByte()
            packetHandler.sendPacket(player, NMS16PacketPlayOutEntityLook(entityId, yRot, xRot, true))
            packetHandler.sendPacket(
                player,
                NMS16PacketPlayOutEntityHeadRotation(),
                "a" to entityId,
                "b" to yRot
            )
        }
    }

    override fun updateEquipment(player: Player, entityId: Int, slot: EquipmentSlot, itemStack: ItemStack) {
        updateEquipment(player, entityId, mapOf(slot to itemStack))
    }

    override fun updateEquipment(player: Player, entityId: Int, equipment: Map<EquipmentSlot, ItemStack>) {
        when {
            majorLegacy >= 11700 -> {
                packetHandler.sendPacket(
                    player,
                    NMSPacketPlayOutEntityEquipment(entityId, equipment.map { Pair(it.key.toNMSEnumItemSlot(), CraftItemStack19.asNMSCopy(it.value)) })
                )
            }

            else -> {
                equipment.forEach { (k, v) ->
                    packetHandler.sendPacket(
                        player,
                        NMS13PacketPlayOutEntityEquipment(entityId, k.toNMS13EnumItemSlot(), CraftItemStack13.asNMSCopy(v))
                    )
                }
            }
        }
    }

    override fun updatePassengers(player: Player, entityId: Int, vararg passengers: Int) {
        val packet = NMSPacketPlayOutMount::class.java.unsafeInstance()
        if (isUniversal) {
            packetHandler.sendPacket(
                player,
                packet,
                "vehicle" to entityId,
                "passengers" to passengers
            )
        } else {
            packetHandler.sendPacket(
                player,
                packet,
                "a" to entityId,
                "b" to passengers
            )
        }
    }

    override fun updateEntityMetadata(player: Player, entityId: Int, vararg metadata: MinecraftMeta) {
        val packet = NMSPacketPlayOutEntityMetadata::class.java.unsafeInstance()
        if (isUniversal) {
            packetHandler.sendPacket(
                player,
                packet,
                "id" to entityId,
                "packedItems" to metadata.map { it.source() }.toList()
            )
        } else {
            packetHandler.sendPacket(
                player,
                packet,
                "a" to entityId,
                "b" to metadata.map { it.source() }.toList()
            )
        }
    }

    override fun updateEntityAnimation(player: Player, entityId: Int, animation: BukkitAnimation) {
        val packet = NMSPacketPlayOutAnimation::class.java.unsafeInstance()
        if (isUniversal) {
            packetHandler.sendPacket(
                player,
                packet,
                "id" to entityId,
                "action" to animation.id
            )
        } else {
            packetHandler.sendPacket(
                player,
                packet,
                "a" to entityId,
                "b" to animation.id
            )
        }
    }

    override fun updateEntityAttach(player: Player, attached: Int, holding: Int) {
        val packet = NMSPacketPlayOutAttachEntity::class.java.unsafeInstance()
        if (isUniversal) {
            packetHandler.sendPacket(
                player,
                packet,
                "sourceId" to attached,
                "destId" to holding
            )
        } else {
            packetHandler.sendPacket(
                player,
                packet,
                "a" to attached,
                "b" to holding
            )
        }
    }

    override fun updatePlayerSleeping(player: Player, entityId: Int, location: Location) {
        packetHandler.sendPacket(
            player,
            NMS13PacketPlayOutBed(),
            "a" to entityId,
            "b" to NMS13BlockPosition(location.blockX, location.blockY, location.blockZ)
        )
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