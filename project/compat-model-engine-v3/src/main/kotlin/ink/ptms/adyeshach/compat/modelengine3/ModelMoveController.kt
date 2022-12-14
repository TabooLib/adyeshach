package ink.ptms.adyeshach.compat.modelengine3

import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController
import ink.ptms.adyeshach.core.entity.EntityInstance
import org.bukkit.entity.Entity
import org.bukkit.util.Vector

class ModelMoveController(val entityInstance: EntityInstance) : MoveController {

    override fun move(p0: Float, p1: Float, p2: Float) {
        TODO("Not yet implemented")
    }

    override fun jump() {
        TODO("Not yet implemented")
    }

    override fun setVelocity(p0: Double, p1: Double, p2: Double) {
        TODO("Not yet implemented")
    }

    override fun addVelocity(p0: Double, p1: Double, p2: Double) {
        TODO("Not yet implemented")
    }

    override fun nullifyFallDistance() {
        TODO("Not yet implemented")
    }

    override fun movePassenger(p0: Entity?, p1: Double, p2: Double, p3: Double) {
        TODO("Not yet implemented")
    }

    override fun isOnGround(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getSpeed(): Float {
        TODO("Not yet implemented")
    }

    override fun getVelocity(): Vector {
        TODO("Not yet implemented")
    }
}