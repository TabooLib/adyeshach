package ink.ptms.adyeshach.api.nms

import ink.ptms.adyeshach.common.bukkit.BukkitDirection
import ink.ptms.adyeshach.common.bukkit.BukkitPaintings
import ink.ptms.adyeshach.common.bukkit.BukkitParticles
import ink.ptms.adyeshach.common.bukkit.BukkitPose
import ink.ptms.adyeshach.common.bukkit.data.VillagerData
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.bukkit.BukkitAnimation
import ink.ptms.adyeshach.core.bukkit.data.GameProfile
import ink.ptms.adyeshach.core.util.getEnum
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
import taboolib.common5.cshort
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * @author Arasple
 * @date 2020/8/3 21:52
 */
@Deprecated("Outdated but usable")
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

    abstract fun teleportEntity(player: Player, entityId: Int, location: Location): CompletableFuture<*>

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

    abstract fun getMetaEntityOptVector(index: Int, value: Vector?): Any

    abstract fun getMetaEntityOptBlockData(index: Int, value: MaterialData?): Any

    abstract fun getMetaEntityOptChatBaseComponent(index: Int, name: String?): Any

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

        @JvmStatic
        val INSTANCE = object : NMS() {

            override fun spawnEntity(player: Player, entityType: EntityTypes, entityId: Int, uuid: UUID, location: Location) {
                Adyeshach.api().getMinecraftAPI().getEntitySpawner().spawnEntity(player, entityType.v2(), entityId, uuid, location)
            }

            override fun spawnEntityLiving(player: Player, entityType: EntityTypes, entityId: Int, uuid: UUID, location: Location) {
                Adyeshach.api().getMinecraftAPI().getEntitySpawner().spawnEntityLiving(player, entityType.v2(), entityId, uuid, location)
            }

            override fun spawnNamedEntity(player: Player, entityId: Int, uuid: UUID, location: Location) {
                Adyeshach.api().getMinecraftAPI().getEntitySpawner().spawnNamedEntity(player, entityId, uuid, location)
            }

            override fun spawnEntityFallingBlock(player: Player, entityId: Int, uuid: UUID, location: Location, material: Material, data: Byte) {
                Adyeshach.api().getMinecraftAPI().getEntitySpawner().spawnEntityFallingBlock(player, entityId, uuid, location, material, data)
            }

            override fun spawnEntityExperienceOrb(player: Player, entityId: Int, location: Location, amount: Int) {
                Adyeshach.api().getMinecraftAPI().getEntitySpawner().spawnEntityExperienceOrb(player, entityId, location, amount)
            }

            override fun spawnEntityPainting(
                player: Player,
                entityId: Int,
                uuid: UUID,
                location: Location,
                direction: BukkitDirection,
                painting: BukkitPaintings
            ) {
                Adyeshach.api().getMinecraftAPI().getEntitySpawner().spawnEntityPainting(player, entityId, uuid, location, direction.v2(), painting.v2())
            }

            override fun addPlayerInfo(player: Player, uuid: UUID, name: String, ping: Int, texture: Array<String>) {
                val profile = GameProfile()
                profile.name = name
                profile.ping = ping
                profile.texture = texture
                Adyeshach.api().getMinecraftAPI().getEntityPlayerHandler().addPlayerInfo(player, uuid, profile)
            }

            override fun removePlayerInfo(player: Player, uuid: UUID) {
                Adyeshach.api().getMinecraftAPI().getEntityPlayerHandler().removePlayerInfo(player, uuid)
            }

            override fun destroyEntity(player: Player, entityId: Int) {
                Adyeshach.api().getMinecraftAPI().getEntityOperator().destroyEntity(player, entityId)
            }

            override fun teleportEntity(player: Player, entityId: Int, location: Location): CompletableFuture<*> {
                Adyeshach.api().getMinecraftAPI().getEntityOperator().teleportEntity(player, entityId, location)
                return CompletableFuture.completedFuture(null)
            }

            override fun relMoveEntity(player: Player, entityId: Int, x: Double, y: Double, z: Double) {
                Adyeshach.api().getMinecraftAPI().getEntityOperator().updateRelEntityMove(player, entityId, x.cshort, y.cshort, z.cshort, true)
            }

            override fun updateEntityVelocity(player: Player, entityId: Int, vector: Vector) {
                Adyeshach.api().getMinecraftAPI().getEntityOperator().updateEntityVelocity(player, entityId, vector)
            }

            override fun setHeadRotation(player: Player, entityId: Int, yaw: Float, pitch: Float) {
                Adyeshach.api().getMinecraftAPI().getEntityOperator().updateHeadRotation(player, entityId, yaw)
            }

            override fun updateEquipment(player: Player, entityId: Int, slot: EquipmentSlot, itemStack: ItemStack) {
                Adyeshach.api().getMinecraftAPI().getEntityOperator().updateEquipment(player, entityId, slot, itemStack)
            }

            override fun updatePassengers(player: Player, entityId: Int, vararg passengers: Int) {
                Adyeshach.api().getMinecraftAPI().getEntityOperator().updatePassengers(player, entityId, *passengers)
            }

            override fun updateEntityMetadata(player: Player, entityId: Int, vararg objects: Any) {
                Adyeshach.api().getMinecraftAPI().getEntityOperator().updateEntityMetadata(player, entityId, objects.map {
                    object : MinecraftMeta {

                        override fun source(): Any {
                            return it
                        }
                    }
                })
            }

            override fun getMetaEntityInt(index: Int, value: Int): Any {
                return Adyeshach.api().getMinecraftAPI().getEntityMetadataHandler().createIntMeta(index, value).source()
            }

            override fun getMetaEntityFloat(index: Int, value: Float): Any {
                return Adyeshach.api().getMinecraftAPI().getEntityMetadataHandler().createFloatMeta(index, value).source()
            }

            override fun getMetaEntityString(index: Int, value: String): Any {
                return Adyeshach.api().getMinecraftAPI().getEntityMetadataHandler().createStringMeta(index, value).source()
            }

            override fun getMetaEntityBoolean(index: Int, value: Boolean): Any {
                return Adyeshach.api().getMinecraftAPI().getEntityMetadataHandler().createBooleanMeta(index, value).source()
            }

            override fun getMetaEntityParticle(index: Int, value: BukkitParticles): Any {
                val v2 = ink.ptms.adyeshach.core.bukkit.BukkitParticles::class.java.getEnum(value.name)
                return Adyeshach.api().getMinecraftAPI().getEntityMetadataHandler().createParticleMeta(index, v2).source()
            }

            override fun getMetaEntityByte(index: Int, value: Byte): Any {
                return Adyeshach.api().getMinecraftAPI().getEntityMetadataHandler().createByteMeta(index, value).source()
            }

            override fun getMetaEntityEulerAngle(index: Int, value: EulerAngle): Any {
                return Adyeshach.api().getMinecraftAPI().getEntityMetadataHandler().createEulerAngleMeta(index, value).source()
            }

            override fun getMetaEntityOptVector(index: Int, value: Vector?): Any {
                return Adyeshach.api().getMinecraftAPI().getEntityMetadataHandler().createOptBlockPosMeta(index, value).source()
            }

            override fun getMetaEntityOptBlockData(index: Int, value: MaterialData?): Any {
                return Adyeshach.api().getMinecraftAPI().getEntityMetadataHandler().createOptBlockStateMeta(index, value).source()
            }

            override fun getMetaEntityOptChatBaseComponent(index: Int, name: String?): Any {
                return Adyeshach.api().getMinecraftAPI().getEntityMetadataHandler().createOptChatMeta(index, name).source()
            }

            override fun getMetaItem(index: Int, itemStack: ItemStack): Any {
                return Adyeshach.api().getMinecraftAPI().getEntityMetadataHandler().createItemStackMeta(index, itemStack).source()
            }

            override fun getMetaVillagerData(index: Int, villagerData: VillagerData): Any {
                return Adyeshach.api().getMinecraftAPI().getEntityMetadataHandler().createVillagerDataMeta(index, villagerData.v2()).source()
            }

            override fun getMetaEntityPose(index: Int, pose: BukkitPose): Any {
                val v2 = ink.ptms.adyeshach.core.bukkit.BukkitPose::class.java.getEnum(pose.name)
                return Adyeshach.api().getMinecraftAPI().getEntityMetadataHandler().createPoseMeta(index, v2).source()
            }

            override fun getEntityTypeNMS(entityTypes: EntityTypes): Any {
                return Adyeshach.api().getMinecraftAPI().getHelper().adapt(entityTypes.v2())
            }

            override fun getBlockPositionNMS(location: Location): Any {
                return Adyeshach.api().getMinecraftAPI().getHelper().adapt(location)
            }

            override fun getPaintingNMS(bukkitPaintings: BukkitPaintings): Any {
                val v2 = ink.ptms.adyeshach.core.bukkit.BukkitPaintings::class.java.getEnum(bukkitPaintings.name)
                return Adyeshach.api().getMinecraftAPI().getHelper().adapt(v2)
            }

            override fun getParticleNMS(bukkitParticles: BukkitParticles): Any {
                val v2 = ink.ptms.adyeshach.core.bukkit.BukkitParticles::class.java.getEnum(bukkitParticles.name)
                return Adyeshach.api().getMinecraftAPI().getHelper().adapt(v2)
            }

            override fun getEntityDataWatcher(entity: Entity): Any {
                return Adyeshach.api().getMinecraftAPI().getHelper().getEntityDataWatcher(entity)
            }

            override fun toBlockId(materialData: MaterialData): Int {
                return Adyeshach.api().getMinecraftAPI().getHelper().getBlockId(materialData)
            }

            override fun getEntity(world: World, id: Int): Entity? {
                return Adyeshach.api().getMinecraftAPI().getHelper().getEntity(world, id)
            }

            override fun parseVec3d(obj: Any): Vector {
                return Adyeshach.api().getMinecraftAPI().getHelper().vec3dToVector(obj)
            }

            override fun getBlockHeight(block: Block): Double {
                return 1.0
            }

            override fun sendAnimation(player: Player, id: Int, type: Int) {
                Adyeshach.api().getMinecraftAPI().getEntityOperator().updateEntityAnimation(player, id, BukkitAnimation.values()[type])
            }

            override fun sendAttachEntity(player: Player, attached: Int, holding: Int) {
                Adyeshach.api().getMinecraftAPI().getEntityOperator().updateEntityAttach(player, attached, holding)
            }

            override fun sendPlayerSleeping(player: Player, id: Int, location: Location) {
                Adyeshach.api().getMinecraftAPI().getEntityOperator().updatePlayerSleeping(player, id, location)
            }

            override fun getTropicalFishPattern(data: Int): TropicalFish.Pattern {
                return Adyeshach.api().getMinecraftAPI().getHelper().adaptTropicalFishPattern(data)
            }

            override fun getTropicalFishDataValue(pattern: TropicalFish.Pattern): Int {
                return Adyeshach.api().getMinecraftAPI().getHelper().adaptTropicalFishPattern(pattern)
            }
        }
    }
}