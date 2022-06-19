package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.common.entity.Controllable
import ink.ptms.adyeshach.common.entity.ai.Controller
import ink.ptms.adyeshach.common.entity.path.PathType
import org.bukkit.Location

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.DefaultControllable
 *
 * @author 坏黑
 * @since 2022/6/19 21:57
 */
interface DefaultControllable : Controllable {

    override var isFreeze: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}

    override var moveSpeed: Double
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun getController(): List<Controller> {
        TODO("Not yet implemented")
    }

    override fun <T : Controller> getController(controller: Class<T>): T? {
        TODO("Not yet implemented")
    }

    override fun registerController(controller: Controller) {
        TODO("Not yet implemented")
    }

    override fun <T : Controller> unregisterController(controller: Class<T>) {
        TODO("Not yet implemented")
    }

    override fun resetController() {
        TODO("Not yet implemented")
    }

    override fun isControllerMoving(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isControllerJumping(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isControllerOnGround(): Boolean {
        TODO("Not yet implemented")
    }

    override fun controllerLook(location: Location, smooth: Boolean, smoothInternal: Float) {
        TODO("Not yet implemented")
    }

    override fun controllerLook(yaw: Float, pitch: Float, smooth: Boolean, smoothInternal: Float) {
        TODO("Not yet implemented")
    }

    override fun controllerMove(location: Location, pathType: PathType, speed: Double) {
        TODO("Not yet implemented")
    }

    override fun controllerStill() {
        TODO("Not yet implemented")
    }

    override fun isTryMoving(): Boolean {
        TODO("Not yet implemented")
    }
}