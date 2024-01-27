package ink.ptms.adyeshach.compat.modelengine4

import com.ticxo.modelengine.api.entity.BaseEntity
import com.ticxo.modelengine.api.entity.Dummy
import com.ticxo.modelengine.api.entity.data.IEntityData
import com.ticxo.modelengine.api.nms.entity.EntityHandler
import com.ticxo.modelengine.api.nms.entity.wrapper.BodyRotationController
import com.ticxo.modelengine.api.nms.entity.wrapper.LookController
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController
import com.ticxo.modelengine.api.nms.impl.EmptyBodyRotationController
import com.ticxo.modelengine.api.nms.impl.EmptyLookController
import com.ticxo.modelengine.api.nms.impl.EmptyMoveController
import ink.ptms.adyeshach.core.entity.EntityInstance
import org.bukkit.entity.Entity
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.EquipmentSlot
import java.util.*

/**
 * Adyeshach
 * ink.ptms.adyeshach.compat.modelengine3.EntityModeled
 *
 * @author 坏黑
 * @since 2024/1/14 22:30
 */
class EntityModeled(val entityInstance: EntityInstance) : Dummy<EntityInstance>() {

    override fun getOriginal(): EntityInstance {
        return entityInstance
    }

    override fun isVisible(): Boolean {
        return true
    }

    override fun setVisible(p0: Boolean) {
    }

    override fun isRemoved(): Boolean {
        return entityInstance.isRemoved
    }

    override fun isAlive(): Boolean {
        return !entityInstance.isRemoved
    }

    override fun isForcedAlive(): Boolean {
        return true
    }

    override fun setForcedAlive(p0: Boolean) {
    }

    override fun getEntityId(): Int {
        return entityInstance.index
    }

    override fun getUUID(): UUID {
        return entityInstance.normalizeUniqueId
    }

    override fun getMaxStepHeight(): Double {
        return 0.5
    }

    override fun setMaxStepHeight(p0: Double) {
    }

    override fun getRenderRadius(): Int {
        return 64
    }

    override fun setRenderRadius(p0: Int) {
    }

    override fun setCollidableWith(p0: Entity?, p1: Boolean) {
    }

    override fun getBodyRotationController(): BodyRotationController {
        return EmptyBodyRotationController()
    }

    override fun getMoveController(): MoveController {
        return EmptyMoveController()
    }

    override fun getLookController(): LookController {
        return EmptyLookController()
    }

    override fun hurt(p0: HumanEntity?, p1: Any?, p2: Float): Boolean {
        return false
    }

    override fun interact(p0: HumanEntity?, p1: EquipmentSlot?): EntityHandler.InteractionResult {
        return EntityHandler.InteractionResult.FAIL
    }

    override fun getYRot(): Float {
        return entityInstance.yaw
    }

    override fun getYHeadRot(): Float {
        return entityInstance.yaw
    }

    override fun getXHeadRot(): Float {
        return entityInstance.pitch
    }

    override fun getYBodyRot(): Float {
        return entityInstance.yaw
    }

    override fun isWalking(): Boolean {
        return false
    }

    override fun isStrafing(): Boolean {
        return false
    }

    override fun isJumping(): Boolean {
        return false
    }

    override fun isFlying(): Boolean {
        return false
    }
}