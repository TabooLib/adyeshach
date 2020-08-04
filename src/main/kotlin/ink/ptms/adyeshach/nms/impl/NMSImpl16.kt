package ink.ptms.adyeshach.nms.impl

import com.mojang.authlib.GameProfile
import ink.ptms.adyeshach.common.bukkit.BukkitParticles
import ink.ptms.adyeshach.nms.NMS
import io.izzel.taboolib.module.lite.SimpleEquip
import io.izzel.taboolib.module.lite.SimpleReflection
import net.minecraft.server.v1_16_R1.*
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.util.EulerAngle
import java.util.*


/**
 * @author Arasple
 * @date 2020/8/3 21:51
 */
class NMSImpl16 : NMS() {

    override fun spawnEntity(player: Player, entityType: Any, entityId: Int, uuid: UUID, location: Location) {
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
                Pair("k", IRegistry.ENTITY_TYPE.a(entityType as EntityTypes<*>))
        )
    }

    override fun spawnEntityLiving(player: Player, entityType: Any, entityId: Int, uuid: UUID, location: Location) {
        sendPacket(
                player,
                PacketPlayOutSpawnEntityLiving(),
                Pair("a", entityId),
                Pair("b", uuid),
                Pair("c", IRegistry.ENTITY_TYPE.a(entityType as EntityTypes<*>)),
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

    override fun addPlayerInfo(player: Player, uuid: UUID) {
        val profile = GameProfile(uuid, "Bakurit_Maueic")
        val infoData = PacketPlayOutPlayerInfo()

        sendPacket(
                player,
                infoData,
                Pair("a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER),
                Pair("b",
                        listOf(
                                infoData.PlayerInfoData(
                                        profile,
                                        60,
                                        EnumGamemode.SURVIVAL,
                                        ChatComponentText("Bakurit_Maueic")
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
                Pair("b",
                        listOf(
                                infoData.PlayerInfoData(
                                        GameProfile(uuid, "Bakurit_Maueic"),
                                        -1,
                                        null,
                                        null
                                )
                        )
                )
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
                        (x * 4096).toInt().toShort(),
                        (y * 4096).toInt().toShort(),
                        (z * 4096).toInt().toShort(),
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

    override fun updateDisplayName(player: Player, entityId: Int, name: String) = updateEntityMetadata(player, entityId, getMetaEntityCustomName(name))

    override fun updateEquipment(player: Player, entityId: Int, slot: EquipmentSlot, itemStack: ItemStack) {
        sendPacket(
                player,
                PacketPlayOutEntityEquipment(
                        entityId,
                        listOf(
                                com.mojang.datafixers.util.Pair(EnumItemSlot.fromName(SimpleEquip.fromBukkit(slot).nms), CraftItemStack.asNMSCopy(itemStack))
                        )
                )
        )
    }

    override fun updateEntityMetadata(player: Player, entityId: Int, vararg objects: Any) {
        val items = mutableListOf<DataWatcher.Item<*>>()
        for (obj in objects) {
            items.add(obj as DataWatcher.Item<*>)
        }
        sendPacket(
                player,
                PacketPlayOutEntityMetadata(),
                Pair("a", entityId),
                Pair("b", items)
        )
    }

    override fun getMetaEntityItemStack(itemStack: ItemStack): Any {
        TODO("Not yet implemented")
    }

    override fun getMetaEntityProperties(onFire: Boolean, crouched: Boolean, unUsedRiding: Boolean, sprinting: Boolean, swimming: Boolean, invisible: Boolean, glowing: Boolean, flyingElytra: Boolean): Any {
        var bits = 0
        if (onFire) bits += 0x01
        if (crouched) bits += 0x02
        if (unUsedRiding) bits += 0x04
        if (sprinting) bits += 0x08
        if (swimming) bits += 0x10
        if (invisible) bits += 0x20
        if (glowing) bits += 0x40
        if (flyingElytra) bits += 0x80
        return DataWatcher.Item(DataWatcherObject(0, DataWatcherRegistry.a), bits.toByte())
    }

    override fun getMetaEntityGravity(noGravity: Boolean): Any = DataWatcher.Item(DataWatcherObject(5, DataWatcherRegistry.i), noGravity)

    override fun getMetaEntitySilenced(silenced: Boolean): Any = DataWatcher.Item(DataWatcherObject(4, DataWatcherRegistry.i), silenced)

    override fun getMetaEntityCustomNameVisible(visible: Boolean): Any = DataWatcher.Item(DataWatcherObject(3, DataWatcherRegistry.i), visible)

    override fun getMetaEntityCustomName(name: String): Any = DataWatcher.Item<Optional<IChatBaseComponent>>(DataWatcherObject(2, DataWatcherRegistry.f), Optional.of(ChatComponentText(name)))

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
        return DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.i), value)
    }

    override fun getMetaEntityParticle(index: Int, value: BukkitParticles): Any {
        return DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.j), getParticleNMS(value) as ParticleParam?)
    }

    override fun getMetaEntityByte(index: Int, value: Byte): Any {
        return DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.a), value)
    }

    override fun getMetaEntityVector(index: Int, value: EulerAngle): Any {
        return DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.k), Vector3f(value.x.toFloat(), value.y.toFloat(), value.z.toFloat()))
    }

    override fun getEntityTypeNMS(entityTypes: ink.ptms.adyeshach.common.entity.type.EntityTypes): Any {
        return SimpleReflection.getFieldValueChecked(EntityTypes::class.java, null, entityTypes.name, true) ?: EntityTypes.ARMOR_STAND
    }

    override fun getParticleNMS(bukkitParticles: BukkitParticles): Any {
        return SimpleReflection.getFieldValueChecked(Particles::class.java, null, bukkitParticles.name, true) ?: Particles.FLAME
    }
}