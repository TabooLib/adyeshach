package ink.ptms.adyeshach.api.nms

import ink.ptms.adyeshach.common.bukkit.BukkitDirection
import ink.ptms.adyeshach.common.bukkit.BukkitPaintings
import ink.ptms.adyeshach.common.bukkit.BukkitParticles
import ink.ptms.adyeshach.common.bukkit.BukkitPose
import ink.ptms.adyeshach.common.bukkit.data.VillagerData
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.TropicalFish
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import org.bukkit.util.EulerAngle
import org.bukkit.util.Vector
import taboolib.module.nms.nmsProxy
import java.util.*

/**
 * @author Arasple
 * @date 2020/8/3 21:52
 */
abstract class NMS {

    abstract fun spawnEntity(player: Player, entityType: EntityTypes, entityId: Int, uuid: UUID, location: Location)

    abstract fun spawnEntityLiving(player: Player, entityType: EntityTypes, entityId: Int, uuid: UUID, location: Location)

    abstract fun spawnNamedEntity(player: Player, entityId: Int, uuid: UUID, location: Location)

    abstract fun spawnEntityFallingBlock(player: Player, entityId: Int, uuid: UUID, location: Location, material: Material, data: Byte)

    abstract fun spawnEntityExperienceOrb(player: Player, entityId: Int, location: Location, amount: Int)

    abstract fun spawnEntityPainting(player: Player, entityId: Int, uuid: UUID, location: Location, direction: BukkitDirection, painting: BukkitPaintings)

    abstract fun addPlayerInfo(player: Player, uuid: UUID, name: String, ping: Int, texture: Array<String>)

    abstract fun removePlayerInfo(player: Player, uuid: UUID)

    abstract fun destroyEntity(player: Player, entityId: Int)

    abstract fun teleportEntity(player: Player, entityId: Int, location: Location)

    abstract fun relMoveEntity(player: Player, entityId: Int, x: Double, y: Double, z: Double)

    abstract fun updateEntityVelocity(player: Player, entityId: Int, vector: Vector)

    abstract fun setHeadRotation(player: Player, entityId: Int, yaw: Float, pitch: Float)

    abstract fun updateEquipment(player: Player, entityId: Int, slot: EquipmentSlot, itemStack: ItemStack)

    abstract fun updatePassengers(player: Player, entityId: Int, vararg passengers: Int)

    abstract fun updateEntityMetadata(player: Player, entityId: Int, vararg objects: Any)

    abstract fun getMetaEntityInt(index: Int, value: Int): Any

    abstract fun getMetaEntityFloat(index: Int, value: Float): Any

    abstract fun getMetaEntityString(index: Int, value: String): Any

    abstract fun getMetaEntityBoolean(index: Int, value: Boolean): Any

    abstract fun getMetaEntityParticle(index: Int, value: BukkitParticles): Any

    abstract fun getMetaEntityByte(index: Int, value: Byte): Any

    abstract fun getMetaEntityEulerAngle(index: Int, value: EulerAngle): Any

    abstract fun getMetaEntityVector(index: Int, value: Vector?): Any

    abstract fun getMetaEntityBlockData(index: Int, value: MaterialData?): Any

    abstract fun getMetaEntityChatBaseComponent(index: Int, name: String?): Any

    abstract fun getMetaItem(index: Int, itemStack: ItemStack): Any

    abstract fun getMetaVillagerData(index: Int, villagerData: VillagerData): Any

    abstract fun getMetaEntityPose(index: Int, pose: BukkitPose): Any

    abstract fun getEntityTypeNMS(entityTypes: EntityTypes): Any

    abstract fun getBlockPositionNMS(location: Location): Any

    abstract fun getPaintingNMS(bukkitPaintings: BukkitPaintings): Any

    abstract fun getParticleNMS(bukkitParticles: BukkitParticles): Any

    abstract fun getEntityDataWatcher(entity: Entity): Any

    abstract fun toBlockId(materialData: MaterialData): Int

    abstract fun getEntity(world: World, id: Int): Entity?

    abstract fun parseVec3d(obj: Any): Vector

    abstract fun getBlockHeight(block: Block): Double

    abstract fun sendAnimation(player: Player, id: Int, type: Int)

    abstract fun sendAttachEntity(player: Player, attached: Int, holding: Int)

    abstract fun sendPlayerSleeping(player: Player, id: Int, location: Location)

    abstract fun getTropicalFishPattern(data: Int): TropicalFish.Pattern

    abstract fun getTropicalFishDataValue(pattern: TropicalFish.Pattern): Int

    companion object {

        val INSTANCE by lazy {
            nmsProxy<NMS>()
        }
    }
}