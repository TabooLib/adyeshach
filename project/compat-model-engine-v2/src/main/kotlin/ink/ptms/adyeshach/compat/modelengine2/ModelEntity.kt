package ink.ptms.adyeshach.compat.modelengine2

import com.ticxo.modelengine.api.model.ActiveModel
import com.ticxo.modelengine.api.model.ModeledEntity
import com.ticxo.modelengine.api.model.base.BaseEntity
import com.ticxo.modelengine.api.model.base.EntityData
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.type.AdyEntityLiving
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector
import java.util.*

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.EntityModeled
 *
 * @author sky
 * @since 2021/9/25 1:35 上午
 */
class EntityModeled(val entityInstance: EntityInstance) : BaseEntity<EntityInstance> {

    val modelUniqueId: UUID = UUID.randomUUID()

    override fun getBase(): EntityInstance {
        return entityInstance
    }

    override fun getLocation(): Location {
        return entityInstance.getLocation()
    }

    override fun getVelocity(): Vector {
        return Vector(0, 0, 0)
//        TODO
//        return entityInstance.getController(GeneralMove::class.java)?.target?.toVector()?.normalize() ?: Vector(0, 0, 0)
    }

    override fun isOnGround(): Boolean {
        return entityInstance.isControllerOnGround()
    }

    override fun getWorld(): World {
        return entityInstance.getWorld()
    }

    override fun getNearbyEntities(v: Double, v1: Double, v2: Double): MutableList<Entity> {
        return world.getNearbyEntities(location, v, v2, v2).toMutableList()
    }

    override fun getEntityId(): Int {
        return entityInstance.index
    }

    override fun remove() {
        return entityInstance.despawn(removeFromManager = true)
    }

    override fun isCustomNameVisible(): Boolean {
        return entityInstance.isCustomNameVisible()
    }

    override fun isDead(): Boolean {
        return if (entityInstance is AdyEntityLiving) entityInstance.getHealth() <= 0 else false
    }

    override fun getUniqueId(): UUID {
        return modelUniqueId
    }

    override fun getType(): EntityType {
        return Adyeshach.api().getEntityTypeHandler().getBukkitEntityType(entityInstance.entityType)
    }

    override fun isInvulnerable(): Boolean {
        return true
    }

    override fun hasGravity(): Boolean {
        return !entityInstance.isNoGravity()
    }

    override fun setGravity(flag: Boolean) {
        return entityInstance.setNoGravity(!flag)
    }

    override fun getHealth(): Double {
        return 1.0
    }

    override fun getMaxHealth(): Double {
        return 1.0
    }

    override fun getCustomName(): String {
        return entityInstance.getCustomName()
    }

    override fun setCustomName(s: String?) {
        return entityInstance.setCustomName(s ?: "")
    }

    override fun getMovementSpeed(): Double {
        return entityInstance.moveSpeed
    }

    override fun getItemInMainHand(): ItemStack {
        return (entityInstance as AdyEntityLiving).getItemInMainHand() ?: ItemStack(Material.AIR)
    }

    override fun getItemInOffHand(): ItemStack {
        return (entityInstance as AdyEntityLiving).getItemInOffHand() ?: ItemStack(Material.AIR)
    }

    override fun isLivingEntity(): Boolean {
        return false
    }

    override fun addPotionEffect(potion: PotionEffect?) {
    }

    override fun removePotionEffect(potion: PotionEffectType?) {
    }

    override fun setEntitySize(width: Float, height: Float, eye: Float) {
    }

    override fun sendDespawnPacket(modeledEntity: ModeledEntity?) {
    }

    override fun sendSpawnPacket(modeledEntity: ModeledEntity?) {
    }

    override fun getLastX(): Double {
        return entityInstance.x
    }

    override fun getLastY(): Double {
        return entityInstance.y
    }

    override fun getLastZ(): Double {
        return entityInstance.z
    }

    override fun getWantedX(): Double {
//        TODO
//        return entityInstance.getController(GeneralMove::class.java)?.target?.x ?: 0.0
        return 0.0
    }

    override fun getWantedY(): Double {
//        TODO
//        return entityInstance.getController(GeneralMove::class.java)?.target?.y ?: 0.0
        return 0.0
    }

    override fun getWantedZ(): Double {
//        TODO
//        return entityInstance.getController(GeneralMove::class.java)?.target?.z ?: 0.0
        return 0.0
    }

    @Deprecated("Deprecated in Java")
    override fun saveModelList(models: MutableMap<String, ActiveModel>) {
    }

    @Deprecated("Deprecated in Java", ReplaceWith("ArrayList()", "java.util.ArrayList"))
    override fun getModelList(): MutableList<String> {
        return ArrayList()
    }

    override fun saveModelInfo(p0: ModeledEntity) {
    }

    override fun loadModelInfo(): EntityData {
        return EntityData()
    }
}