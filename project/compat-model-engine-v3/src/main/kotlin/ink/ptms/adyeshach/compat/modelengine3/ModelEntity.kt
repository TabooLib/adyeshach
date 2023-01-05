package ink.ptms.adyeshach.compat.modelengine3

import com.ticxo.modelengine.api.ModelEngineAPI
import com.ticxo.modelengine.api.entity.BaseEntity
import com.ticxo.modelengine.api.generator.Hitbox
import com.ticxo.modelengine.api.model.IModel
import com.ticxo.modelengine.api.nms.entity.hitbox.ModelHitbox
import com.ticxo.modelengine.api.nms.entity.impl.DefaultBodyRotationController
import com.ticxo.modelengine.api.nms.entity.impl.EmptyLookController
import com.ticxo.modelengine.api.nms.entity.impl.EmptyMoveController
import com.ticxo.modelengine.api.nms.entity.impl.EmptyRangeManager
import com.ticxo.modelengine.api.nms.entity.wrapper.BodyRotationController
import com.ticxo.modelengine.api.nms.entity.wrapper.LookController
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController
import com.ticxo.modelengine.api.nms.entity.wrapper.RangeManager
import com.ticxo.modelengine.api.nms.world.IDamageSource
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.StandardTags
import ink.ptms.adyeshach.core.entity.type.AdyEntityLiving
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
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

    private var stepHeight: Double? = null
    private var hitbox: Hitbox? = null
    private var modelHitbox: ModelHitbox? = null

    override fun getOriginal(): EntityInstance {
        return entityInstance
    }

    override fun wrapMoveControl(): MoveController {
        return EmptyMoveController()
    }

    override fun wrapLookControl(): LookController {
        return EmptyLookController()
    }

    override fun wrapBodyRotationControl(): BodyRotationController {
        val controller = DefaultBodyRotationController(this)
        controller.maxHeadAngle = 75.0f
        controller.maxBodyAngle = 75.0f
        controller.isPlayerMode = true
        return controller
    }

    override fun wrapNavigation() {
    }

    override fun wrapRangeManager(p0: IModel?): RangeManager {
        return EmptyRangeManager()
    }

    override fun getRangeManager(): RangeManager {
        return EmptyRangeManager()
    }

    override fun onHurt(p0: IDamageSource?, p1: Float): Boolean {
        return false
    }

    override fun onInteract(p0: Player?, p1: EquipmentSlot?) {
    }

    override fun setHitbox(box: Hitbox?) {
        hitbox = box ?: Hitbox(original.entitySize.width, original.entitySize.height, original.entitySize.width, original.entitySize.height)
        if (modelHitbox == null) {
            modelHitbox = ModelEngineAPI.getEntityHandler().createModelHitbox(this)
        }
        modelHitbox?.refresh()
    }

    override fun getHitbox(): Hitbox? {
        return hitbox
    }

    override fun getModelHitbox(): ModelHitbox? {
        return modelHitbox
    }

    override fun setStepHeight(p0: Double) {
        stepHeight = p0
    }

    override fun getStepHeight(): Double {
        return stepHeight ?: 0.0
    }

    override fun setCollidableToLiving(p0: LivingEntity?, p1: Boolean) {
    }

    override fun broadcastSpawnPacket() {
        original.respawn()
    }

    override fun broadcastDespawnPacket() {
        original.despawn()
    }

    override fun getEntityId(): Int {
        return original.index
    }

    override fun getUniqueId(): UUID {
        return original.normalizeUniqueId
    }

    override fun getLocation(): Location {
        return original.getLocation()
    }

    override fun getWorld(): World {
        return original.world
    }

    override fun isDead(): Boolean {
        return false
    }

    override fun isGlowing(): Boolean {
        return original.isGlowing()
    }

    override fun isOnGround(): Boolean {
        return true
    }

    override fun isMoving(): Boolean {
        return original.hasTag(StandardTags.IS_MOVING)
    }

    override fun setYHeadRot(p0: Float) {
    }

    override fun getYHeadRot(): Float {
        return original.yaw
    }

    override fun getXHeadRot(): Float {
        return original.pitch
    }

    override fun setYBodyRot(p0: Float) {
    }

    override fun getYBodyRot(): Float {
        return original.yaw
    }

    override fun getPassengers(): MutableList<Entity> {
        return arrayListOf()
    }

    override fun getItemInSlot(p0: EquipmentSlot): ItemStack {
        entityInstance as AdyEntityLiving
        return entityInstance.getEquipment(p0) ?: ItemStack(Material.AIR)
    }
}