package ink.ptms.adyeshach.api.nms.impl

import com.google.common.base.Enums
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.bukkit.BukkitDirection
import ink.ptms.adyeshach.common.bukkit.BukkitPaintings
import ink.ptms.adyeshach.common.bukkit.BukkitParticles
import io.izzel.taboolib.module.lite.SimpleEquip
import net.minecraft.server.v1_10_R1.*
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.util.EulerAngle
import java.util.*


/**
 * @author Arasple
 * @date 2020/8/6 18:40
 */
class NMSImpl10 : NMS() {

    override fun spawnEntity(player: Player, entityType: Any, entityId: Int, uuid: UUID, location: Location, vararg data: Int) {
        sendPacket(
            player,
            PacketPlayOutSpawnEntity(),
            Pair("a", entityId),
            Pair("b", uuid),
            Pair("c", location.x),
            Pair("d", location.y),
            Pair("e", location.z),
            Pair("f", (location.yaw * 256.0f / 360.0f).toInt().toByte()),
            Pair("g", (location.pitch * 256.0f / 360.0f).toInt().toByte()),
            Pair("k", entityType)
        )
    }

    override fun spawnEntityLiving(player: Player, entityType: Any, entityId: Int, uuid: UUID, location: Location) {
        sendPacket(
            player,
            PacketPlayOutSpawnEntityLiving(),
            Pair("a", entityId),
            Pair("b", uuid),
            Pair("c", entityType),
            Pair("d", location.x),
            Pair("e", location.y),
            Pair("f", location.z),
            Pair("g", 0),
            Pair("h", 0),
            Pair("i", 0),
            Pair("j", (location.yaw * 256.0f / 360.0f).toInt().toByte()),
            Pair("k", (location.pitch * 256.0f / 360.0f).toInt().toByte()),
            Pair("l", (location.yaw * 256.0f / 360.0f).toInt().toByte())
        )
    }

    override fun spawnNamedEntity(player: Player, entityType: Any, entityId: Int, uuid: UUID, location: Location) {
        sendPacket(
            player,
            PacketPlayOutNamedEntitySpawn(),
            Pair("a", entityId),
            Pair("b", uuid),
            Pair("c", location.x),
            Pair("d", location.y),
            Pair("e", location.z),
            Pair("f", (location.yaw * 256 / 360).toInt().toByte()),
            Pair("g", (location.pitch * 256 / 360).toInt().toByte())
        )
    }

    override fun spawnEntityExperienceOrb(player: Player, entityId: Int, location: Location, amount: Int) {
        sendPacket(
            player,
            PacketPlayOutSpawnEntityExperienceOrb(),
            Pair("a", entityId),
            Pair("b", location.x),
            Pair("c", location.y),
            Pair("d", location.z),
            Pair("e", amount),
        )
    }

    override fun spawnEntityPainting(player: Player, entityId: Int, uuid: UUID, location: Location, direction: BukkitDirection, painting: BukkitPaintings) {
        sendPacket(
            player,
            PacketPlayOutSpawnEntityPainting(),
            Pair("a", entityId),
            Pair("b", uuid),
            Pair("c", getBlockPositionNMS(location)),
            Pair("d", EnumDirection.valueOf(direction.name)),
            Pair("e", getPaintingNMS(painting))
        )
    }

    override fun addPlayerInfo(player: Player, uuid: UUID, name: String, ping: Int, gameMode: GameMode, texture: Array<String>) {
        val profile = GameProfile(uuid, name)
        val infoData = PacketPlayOutPlayerInfo()
        if (texture.size == 2) {
            profile.properties.put("textures", Property("textures", texture[0], texture[1]))
        }
        sendPacket(
            player,
            infoData,
            Pair("a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER),
            Pair(
                "b",
                listOf(
                    infoData.PlayerInfoData(
                        profile,
                        ping,
                        Enums.getIfPresent(EnumGamemode::class.java, gameMode.name).or(EnumGamemode.NOT_SET),
                        ChatComponentText(name)
                    )
                )
            )
        )
    }

    override fun removePlayerInfo(player: Player, uuid: UUID) {
        val infoData = PacketPlayOutPlayerInfo()
        sendPacket(
            player,
            infoData,
            Pair("a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER),
            Pair("b", listOf(infoData.PlayerInfoData(GameProfile(uuid, ""), -1, null, null)))
        )
    }

    override fun destroyEntity(player: Player, entityId: Int) = sendPacket(player, PacketPlayOutEntityDestroy(entityId))

    override fun teleportEntity(player: Player, entityId: Int, location: Location) {
        sendPacket(
            player,
            PacketPlayOutEntityTeleport(),
            Pair("a", entityId),
            Pair("b", location.x),
            Pair("c", location.y),
            Pair("d", location.z),
            Pair("e", (location.yaw * 256 / 360).toInt().toByte()),
            Pair("f", (location.pitch * 256 / 360).toInt().toByte()),
            Pair("g", false) // onGround
        )
    }

    override fun relMoveEntity(player: Player, entityId: Int, x: Double, y: Double, z: Double) {
        sendPacket(
            player,
            PacketPlayOutEntity.PacketPlayOutRelEntityMove(
                entityId,
                x.toLong(),
                y.toLong(),
                z.toLong(),
                true
            )
        )
    }

    override fun setHeadRotation(player: Player, entityId: Int, yaw: Float, pitch: Float) {
        sendPacket(
            player,
            PacketPlayOutEntityHeadRotation(),
            Pair("a", entityId),
            Pair("b", MathHelper.d(yaw * 256.0f / 360.0f).toByte())
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
        sendPacket(
            player,
            PacketPlayOutEntityEquipment(entityId, EnumItemSlot.a(SimpleEquip.fromBukkit(slot).nms), CraftItemStack.asNMSCopy(itemStack))
        )
    }

    override fun updateEntityMetadata(player: Player, entityId: Int, vararg objects: Any) {
        sendPacket(player, PacketPlayOutEntityMetadata(), Pair("a", entityId), Pair("b", objects.map { it as DataWatcher.Item<*> }.toList()))
    }

    override fun getMetaEntityItemStack(itemStack: ItemStack): Any {
        TODO("Not yet implemented")
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
        return DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.h), value)
    }

    override fun getMetaEntityParticle(index: Int, value: BukkitParticles): Any {
        return 0
    }

    override fun getMetaEntityByte(index: Int, value: Byte): Any {
        return DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.a), value)
    }

    override fun getMetaEntityVector(index: Int, value: EulerAngle): Any {
        return DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.i), Vector3f(value.x.toFloat(), value.y.toFloat(), value.z.toFloat()))
    }

    override fun getMetaEntityChatBaseComponent(index: Int, name: String): Any {
        return DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.e), ChatComponentText(name))
    }

    override fun getEntityTypeNMS(entityTypes: ink.ptms.adyeshach.common.entity.type.EntityTypes): Any {
        return entityTypes.bukkitId
    }

    override fun getBlockPositionNMS(location: Location): Any {
        return BlockPosition(location.blockX, location.blockY, location.blockZ)
    }

    override fun getPaintingNMS(bukkitPaintings: BukkitPaintings): Any {
        // TODO 待测试
        return bukkitPaintings.name
    }

    override fun getParticleNMS(bukkitParticles: BukkitParticles): Any {
        return 0
    }

}