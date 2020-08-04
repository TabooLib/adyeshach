package ink.ptms.adyeshach.api.nms

import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.common.bukkit.BukkitParticles
import ink.ptms.adyeshach.common.entity.type.EntityTypes
import io.izzel.taboolib.Version
import io.izzel.taboolib.module.inject.TSchedule
import io.izzel.taboolib.module.lite.SimpleReflection
import io.izzel.taboolib.module.lite.SimpleVersionControl
import io.izzel.taboolib.module.packet.TPacketHandler
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.util.EulerAngle
import java.util.*

/**
 * @author Arasple
 * @date 2020/8/3 21:52
 */
abstract class NMS {

    abstract fun spawnEntity(player: Player, entityType: Any, entityId: Int, uuid: UUID, location: Location)

    abstract fun spawnEntityLiving(player: Player, entityType: Any, entityId: Int, uuid: UUID, location: Location)

    abstract fun spawnNamedEntity(player: Player, entityType: Any, entityId: Int, uuid: UUID, location: Location)

    abstract fun addPlayerInfo(player: Player, uuid: UUID, name: String, ping: Int, gameMode: GameMode, texture: Array<String>)

    abstract fun removePlayerInfo(player: Player, uuid: UUID)

    abstract fun destroyEntity(player: Player, entityId: Int)

    abstract fun teleportEntity(player: Player, entityId: Int, location: Location)

    abstract fun relMoveEntity(player: Player, entityId: Int, x: Double, y: Double, z: Double)

    abstract fun setHeadRotation(player: Player, entityId: Int, yaw: Float, pitch: Float)

    abstract fun updateDisplayName(player: Player, entityId: Int, name: String)

    abstract fun updateEquipment(player: Player, entityId: Int, slot: EquipmentSlot, itemStack: ItemStack)

    abstract fun updateEntityMetadata(player: Player, entityId: Int, vararg objects: Any)

    abstract fun getMetaEntityItemStack(itemStack: ItemStack): Any

    abstract fun getMetaEntityProperties(onFire: Boolean, crouched: Boolean, unUsedRiding: Boolean, sprinting: Boolean, swimming: Boolean, invisible: Boolean, glowing: Boolean, flyingElytra: Boolean): Any

    abstract fun getMetaEntityGravity(noGravity: Boolean): Any

    abstract fun getMetaEntitySilenced(silenced: Boolean): Any

    abstract fun getMetaEntityCustomNameVisible(visible: Boolean): Any

    abstract fun getMetaEntityCustomName(name: String): Any

    abstract fun getMetaEntityInt(index: Int, value: Int): Any

    abstract fun getMetaEntityFloat(index: Int, value: Float): Any

    abstract fun getMetaEntityString(index: Int, value: String): Any

    abstract fun getMetaEntityBoolean(index: Int, value: Boolean): Any

    abstract fun getMetaEntityParticle(index: Int, value: BukkitParticles): Any

    abstract fun getMetaEntityByte(index: Int, value: Byte): Any

    abstract fun getMetaEntityVector(index: Int, value: EulerAngle): Any

    abstract fun getEntityTypeNMS(entityTypes: EntityTypes): Any

    abstract fun getParticleNMS(bukkitParticles: BukkitParticles): Any

    companion object {

        lateinit var INSTANCE: NMS

        @TSchedule
        fun init() {
            val version = when {
                Version.isAfter(Version.v1_13) -> "16"
                Version.isAfter(Version.v1_9) -> "12"
                else -> "8"
            }
            INSTANCE = SimpleVersionControl.createNMS("ink.ptms.adyeshach.api.nms.impl.NMSImpl$version").useNMS().translate(Adyeshach.plugin).getDeclaredConstructor().newInstance() as NMS
        }

        fun sendPacket(player: Player, packet: Any, vararg fields: Pair<String, Any>) = TPacketHandler.sendPacket(player, setFields(packet, *fields))

        private fun setFields(any: Any, vararg fields: Pair<String, Any>): Any {
            fields.forEach { (key, value) ->
                SimpleReflection.setFieldValue(any.javaClass, any, key, value, true)
            }
            return any
        }

    }

}