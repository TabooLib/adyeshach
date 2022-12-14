package ink.ptms.adyeshach.compat.modelengine3

import com.ticxo.modelengine.api.entity.BaseEntity
import com.ticxo.modelengine.api.generator.Hitbox
import com.ticxo.modelengine.api.model.IModel
import com.ticxo.modelengine.api.nms.entity.wrapper.BodyRotationController
import com.ticxo.modelengine.api.nms.entity.wrapper.LookController
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController
import com.ticxo.modelengine.api.nms.entity.wrapper.RangeManager
import com.ticxo.modelengine.api.nms.world.IDamageSource
import ink.ptms.adyeshach.core.entity.EntityInstance
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
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

    val moveController = ModelMoveController(entityInstance)
    val lookController = ModelLookController(entityInstance)
    val bodyRotationController = ModelBodyRotationController(entityInstance)

    override fun getOriginal(): EntityInstance {
        return entityInstance
    }

    override fun wrapMoveControl(): MoveController {
        return moveController
    }

    override fun wrapLookControl(): LookController {
        return lookController
    }

    override fun wrapBodyRotationControl(): BodyRotationController {
        return bodyRotationController
    }

    override fun wrapNavigation() {
        TODO("Not yet implemented")
    }

    override fun wrapRangeManager(p0: IModel?): RangeManager {
        TODO("Not yet implemented")
    }

    override fun getRangeManager(): RangeManager {
        TODO("Not yet implemented")
    }

    override fun onHurt(p0: IDamageSource?, p1: Float): Boolean {
        TODO("Not yet implemented")
    }

    override fun onInteract(p0: Player?, p1: EquipmentSlot?) {
        TODO("Not yet implemented")
    }

    override fun setHitbox(p0: Hitbox?) {
        TODO("Not yet implemented")
    }

    override fun getHitbox(): Hitbox {
        TODO("Not yet implemented")
    }

    override fun setStepHeight(p0: Double) {
        TODO("Not yet implemented")
    }

    override fun getStepHeight(): Double {
        TODO("Not yet implemented")
    }

    override fun setCollidableToLiving(p0: LivingEntity?, p1: Boolean) {
        TODO("Not yet implemented")
    }

    override fun broadcastSpawnPacket() {
        TODO("Not yet implemented")
    }

    override fun broadcastDespawnPacket() {
        TODO("Not yet implemented")
    }

    override fun getEntityId(): Int {
        TODO("Not yet implemented")
    }

    override fun getUniqueId(): UUID {
        TODO("Not yet implemented")
    }

    override fun getLocation(): Location {
        TODO("Not yet implemented")
    }

    override fun getWorld(): World {
        TODO("Not yet implemented")
    }

    override fun isDead(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isGlowing(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isOnGround(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isMoving(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setYHeadRot(p0: Float) {
        TODO("Not yet implemented")
    }

    override fun getYHeadRot(): Float {
        TODO("Not yet implemented")
    }

    override fun getXHeadRot(): Float {
        TODO("Not yet implemented")
    }

    override fun setYBodyRot(p0: Float) {
        TODO("Not yet implemented")
    }

    override fun getYBodyRot(): Float {
        TODO("Not yet implemented")
    }

    override fun getPassengers(): MutableList<Entity> {
        TODO("Not yet implemented")
    }
}