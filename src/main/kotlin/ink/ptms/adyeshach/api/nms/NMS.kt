package ink.ptms.adyeshach.api.nms

import ink.ptms.adyeshach.common.bukkit.BukkitDirection
import ink.ptms.adyeshach.common.bukkit.BukkitPaintings
import ink.ptms.adyeshach.common.bukkit.BukkitParticles
import ink.ptms.adyeshach.common.entity.EntityTypes
import io.izzel.taboolib.module.inject.TInject
import io.izzel.taboolib.module.lite.SimpleReflection
import io.izzel.taboolib.module.nms.impl.Position
import io.izzel.taboolib.module.packet.TPacketHandler
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.util.EulerAngle
import org.bukkit.util.Vector
import java.util.*

/**
 * @author Arasple
 * @date 2020/8/3 21:52
 */
abstract class NMS {

    abstract fun spawnEntity(player: Player, entityType: Any, entityId: Int, uuid: UUID, location: Location)

    abstract fun spawnEntityLiving(player: Player, entityType: Any, entityId: Int, uuid: UUID, location: Location)

    abstract fun spawnNamedEntity(player: Player, entityType: Any, entityId: Int, uuid: UUID, location: Location)

    abstract fun spawnEntityFallingBlock(player: Player, entityId: Int, uuid: UUID, location: Location, material: Material, data: Byte)

    abstract fun spawnEntityExperienceOrb(player: Player, entityId: Int, location: Location, amount: Int)

    abstract fun spawnEntityPainting(player: Player, entityId: Int, uuid: UUID, location: Location, direction: BukkitDirection, painting: BukkitPaintings)

    abstract fun addPlayerInfo(player: Player, uuid: UUID, name: String, ping: Int, gameMode: GameMode, texture: Array<String>)

    abstract fun removePlayerInfo(player: Player, uuid: UUID)

    abstract fun destroyEntity(player: Player, entityId: Int)

    abstract fun teleportEntity(player: Player, entityId: Int, location: Location)

    abstract fun relMoveEntity(player: Player, entityId: Int, x: Double, y: Double, z: Double)

    abstract fun updateEntityVelocity(player: Player, entityId: Int, vector: Vector)

    abstract fun setHeadRotation(player: Player, entityId: Int, yaw: Float, pitch: Float)

    abstract fun updateEquipment(player: Player, entityId: Int, slot: EquipmentSlot, itemStack: ItemStack)

    abstract fun updateEntityMetadata(player: Player, entityId: Int, vararg objects: Any)

    abstract fun getMetaEntityInt(index: Int, value: Int): Any

    abstract fun getMetaEntityFloat(index: Int, value: Float): Any

    abstract fun getMetaEntityString(index: Int, value: String): Any

    abstract fun getMetaEntityBoolean(index: Int, value: Boolean): Any

    abstract fun getMetaEntityParticle(index: Int, value: BukkitParticles): Any

    abstract fun getMetaEntityByte(index: Int, value: Byte): Any

    abstract fun getMetaEntityVector(index: Int, value: EulerAngle): Any

    abstract fun getMetaEntityChatBaseComponent(index: Int, name: String?): Any

    abstract fun getMetaItem(index: Int, itemStack: ItemStack): Any

    abstract fun getEntityTypeNMS(entityTypes: EntityTypes): Any

    abstract fun getBlockPositionNMS(location: Location): Any

    abstract fun getPaintingNMS(bukkitPaintings: BukkitPaintings): Any

    abstract fun getParticleNMS(bukkitParticles: BukkitParticles): Any

    abstract fun getNavigationPathList(mob: Mob, location: Location): MutableList<Position>

    companion object {

        @TInject(asm = "ink.ptms.adyeshach.api.nms.impl.NMSImpl")
        lateinit var INSTANCE: NMS

        fun sendPacket(player: Player, packet: Any, vararg fields: Pair<String, Any>) {
            TPacketHandler.sendPacket(player, setFields(packet, *fields))
        }

        fun setFields(any: Any, vararg fields: Pair<String, Any>): Any {
            fields.forEach { (key, value) ->
                SimpleReflection.setFieldValue(any.javaClass, any, key, value, true)
            }
            return any
        }
    }
}