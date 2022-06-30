package ink.ptms.adyeshach.impl.nms

import ink.ptms.adyeshach.common.api.*
import ink.ptms.adyeshach.common.bukkit.BukkitDirection
import ink.ptms.adyeshach.common.bukkit.BukkitPaintings
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.util.getEnum
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import taboolib.common.reflect.Reflex.Companion.getProperty
import taboolib.common.reflect.Reflex.Companion.invokeMethod
import taboolib.common.reflect.Reflex.Companion.unsafeInstance
import taboolib.module.nms.MinecraftVersion
import java.util.*

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.DefaultMinecraftEntitySpawner
 *
 * @author 坏黑
 * @since 2022/6/28 00:08
 */
class DefaultMinecraftEntitySpawner : MinecraftEntitySpawner {

    val isUniversal = MinecraftVersion.isUniversal

    val majorLegacy = MinecraftVersion.majorLegacy

    val helper: MinecraftHelper
        get() = Adyeshach.api().getMinecraftAPI().getHelper()

    val typeHandler: AdyeshachEntityTypeHandler
        get() = Adyeshach.api().getEntityTypeHandler()

    val packetHandler: MinecraftPacketHandler
        get() = Adyeshach.api().getMinecraftAPI().getPacketHandler()

    val nms16EntityTypesRegistryBlocks: NMS16RegistryBlocks<NMS16EntityTypes<*>>
        get() = NMS16IRegistry::class.java.getProperty("ENTITY_TYPE", fixed = true)!!

    val nms13EntityTypesRegistryBlocks: NMS13IRegistry<NMS13EntityTypes<*>>
        get() = NMS13IRegistry::class.java.getProperty("ENTITY_TYPE", fixed = true)!!

    val nms16Motive: NMS16RegistryBlocks<NMS16Paintings>
        get() = NMS16IRegistry::class.java.getProperty("MOTIVE", fixed = true)!!

    override fun spawnEntity(player: Player, entityType: EntityTypes, entityId: Int, uuid: UUID, location: Location) {
        val packet = NMSPacketPlayOutSpawnEntity::class.java.unsafeInstance()
        val xRot = (location.yaw * 256.0f / 360.0f).toInt().toByte()
        val yRot = (location.pitch * 256.0f / 360.0f).toInt().toByte()
        if (isUniversal) {
            packetHandler.sendPacket(
                player,
                packet,
                "id" to entityId,
                "uuid" to uuid,
                "x" to location.x,
                "y" to location.y,
                "z" to location.z,
                "xa" to 0,
                "ya" to 0,
                "za" to 0,
                "xRot" to xRot,
                "yRot" to yRot,
                "type" to helper.adapt(entityType),
                "data" to 0
            )
        } else {
            packetHandler.sendPacket(
                player,
                packet,
                "a" to entityId,
                "b" to uuid,
                "c" to location.x,
                "d" to location.y,
                "e" to location.z,
                "f" to xRot,
                "g" to yRot,
                "k" to (if (majorLegacy <= 11300) typeHandler.getBukkitEntityId(entityType) else helper.adapt(entityType))
            )
        }
    }

    override fun spawnEntityLiving(player: Player, entityType: EntityTypes, entityId: Int, uuid: UUID, location: Location) {
        // 1.13 以下版本盔甲架子不是 EntityLiving 类型，1.19 以上版本所有实体使用 PacketPlayOutSpawnEntity 数据包生成
        if ((entityType == EntityTypes.ARMOR_STAND && majorLegacy < 11300) || majorLegacy >= 11900) {
            return spawnEntity(player, entityType, entityId, uuid, location)
        }
        val packet = NMS16PacketPlayOutSpawnEntityLiving::class.java.unsafeInstance()
        val yRot = (location.yaw * 256.0f / 360.0f).toInt().toByte()
        val xRot = (location.pitch * 256.0f / 360.0f).toInt().toByte()
        if (isUniversal) {
            packetHandler.sendPacket(
                player,
                packet,
                "id" to entityId,
                "uuid" to uuid,
                "type" to nms16EntityTypesRegistryBlocks.invokeMethod<Int>("getId", helper.adapt(entityType))!!,
                "x" to location.x,
                "y" to location.y,
                "z" to location.z,
                "xd" to 0,
                "yd" to 0,
                "zd" to 0,
                "yRot" to xRot,
                "xRot" to yRot,
                "yHeadRot" to xRot
            )
        } else {
            packetHandler.sendPacket(
                player,
                packet,
                "a" to entityId,
                "b" to uuid,
                "c" to when {
                    majorLegacy >= 11400 -> nms16EntityTypesRegistryBlocks.invokeMethod<Any>("a", helper.adapt(entityType), fixed = true)
                    majorLegacy >= 11300 -> nms13EntityTypesRegistryBlocks.invokeMethod<Any>("a", helper.adapt(entityType), fixed = true)
                    else -> typeHandler.getBukkitEntityId(entityType)
                },
                "d" to location.x,
                "e" to location.y,
                "f" to location.z,
                "g" to 0,
                "h" to 0,
                "i" to 0,
                "j" to xRot,
                "k" to yRot,
                "l" to xRot,
                "m" to if (majorLegacy >= 11500) null else NMS16DataWatcher(null)
            )
        }
    }

    override fun spawnNamedEntity(player: Player, entityId: Int, uuid: UUID, location: Location) {
        val packet = NMS16PacketPlayOutSpawnEntityPlayer::class.java.unsafeInstance()
        val yRot = (location.yaw * 256.0f / 360.0f).toInt().toByte()
        val xRot = (location.pitch * 256.0f / 360.0f).toInt().toByte()
        if (isUniversal) {
            packetHandler.sendPacket(
                player,
                packet,
                "entityId" to entityId,
                "playerId" to uuid,
                "x" to location.x,
                "y" to location.y,
                "z" to location.z,
                "yRot" to yRot,
                "xRot" to xRot
            )
        } else {
            packetHandler.sendPacket(
                player,
                packet,
                "a" to entityId,
                "b" to uuid,
                "c" to location.x,
                "d" to location.y,
                "e" to location.z,
                "f" to yRot,
                "g" to xRot,
                "h" to if (majorLegacy >= 11500) null else NMS16DataWatcher(null),
            )
        }
    }

    override fun spawnEntityFallingBlock(player: Player, entityId: Int, uuid: UUID, location: Location, material: Material, data: Byte) {
        val packet = NMS16PacketPlayOutSpawnEntity::class.java.unsafeInstance()
        val xRot = (location.yaw * 256.0f / 360.0f).toInt().toByte()
        val yRot = (location.pitch * 256.0f / 360.0f).toInt().toByte()
        if (isUniversal) {
            val block = NMSBlocks::class.java.getProperty<Any>(material.name, fixed = true)
            packetHandler.sendPacket(
                player,
                packet,
                "id" to entityId,
                "uuid" to uuid,
                "x" to location.x,
                "y" to location.y,
                "z" to location.z,
                "xa" to 0,
                "ya" to 0,
                "za" to 0,
                "xRot" to xRot,
                "yRot" to yRot,
                "type" to helper.adapt(EntityTypes.FALLING_BLOCK),
                "data" to NMSBlock.getId(((block ?: NMSBlocks.STONE) as NMSBlock).defaultBlockState())
            )
        } else if (majorLegacy >= 11300) {
            val block = NMS16Blocks::class.java.getProperty<Any>(material.name, fixed = true)
            packetHandler.sendPacket(
                player,
                packet,
                "a" to entityId,
                "b" to uuid,
                "c" to location.x,
                "d" to location.y,
                "e" to location.z,
                "f" to xRot,
                "g" to yRot,
                "k" to helper.adapt(EntityTypes.FALLING_BLOCK),
                "l" to NMS16Block.getCombinedId(((block ?: NMS16Blocks.STONE) as NMS16Block).blockData)
            )
        } else {
            packetHandler.sendPacket(
                player,
                packet,
                "a" to entityId,
                "b" to uuid,
                "c" to location.x,
                "d" to location.y,
                "e" to location.z,
                "f" to xRot,
                "g" to yRot,
                "k" to helper.adapt(EntityTypes.FALLING_BLOCK),
                "l" to material.id + (data.toInt() shl 12)
            )
        }
    }

    override fun spawnEntityExperienceOrb(player: Player, entityId: Int, location: Location, amount: Int) {
        val packet = NMS16PacketPlayOutSpawnEntityExperienceOrb::class.java.unsafeInstance()
        if (isUniversal) {
            packetHandler.sendPacket(
                player,
                packet,
                "id" to entityId,
                "x" to location.x,
                "y" to location.y,
                "z" to location.z,
                "value" to amount,
            )
        } else {
            packetHandler.sendPacket(
                player,
                packet,
                "a" to entityId,
                "b" to location.x,
                "c" to location.y,
                "d" to location.z,
                "e" to amount,
            )
        }
    }

    override fun spawnEntityPainting(player: Player, entityId: Int, uuid: UUID, location: Location, direction: BukkitDirection, painting: BukkitPaintings) {
        if (MinecraftVersion.majorLegacy >= 11900) {
            error("spawnEntityPainting() is not supported in this version")
        }
        val packet = NMS9PacketPlayOutSpawnEntityPainting::class.java.unsafeInstance()
        val art = helper.adapt(painting)
        if (isUniversal) {
            packetHandler.sendPacket(
                player,
                packet,
                "id" to entityId,
                "uuid" to uuid,
                "pos" to helper.adapt(location),
                "direction" to NMSEnumDirection::class.java.getEnum(direction.name),
                "motive" to nms16Motive.invokeMethod<Any>("a", art)
            )
        } else if (majorLegacy > 11300) {
            packetHandler.sendPacket(
                player,
                packet,
                "a" to entityId,
                "b" to uuid,
                "c" to helper.adapt(location),
                "d" to NMS16EnumDirection::class.java.getEnum(direction.name),
                "e" to nms16Motive.invokeMethod<Any>("a", art)
            )
        } else {
            packetHandler.sendPacket(
                player,
                packet,
                "a" to entityId,
                "b" to uuid,
                "c" to helper.adapt(location),
                "d" to NMS9EnumDirection::class.java.getEnum(direction.name),
                "e" to art
            )
        }
    }
}