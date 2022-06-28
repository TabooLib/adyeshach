package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.api.event.AdyeshachControllerAddEvent
import ink.ptms.adyeshach.api.event.AdyeshachControllerRemoveEvent
import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.entity.Controllable
import ink.ptms.adyeshach.common.entity.TagContainer
import ink.ptms.adyeshach.common.entity.ai.Controller
import ink.ptms.adyeshach.common.entity.ai.general.GeneralGravity
import ink.ptms.adyeshach.common.entity.ai.general.GeneralMove
import ink.ptms.adyeshach.common.entity.ai.general.GeneralSmoothLook
import ink.ptms.adyeshach.common.util.path.PathFinderHandler
import ink.ptms.adyeshach.common.entity.path.PathType
import ink.ptms.adyeshach.common.util.path.ResultNavigation
import ink.ptms.adyeshach.common.util.errorBy
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
        set(value) {
            this as TagContainer
            setTag("isFreeze", value.toString())
        }
        get() {
            this as TagContainer
            return hasTag("isFreeze")
        }

    override fun getController(): List<Controller> {
        this as DefaultEntityInstance
        return controller.toList()
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Controller> getController(controller: Class<T>): T? {
        this as DefaultEntityInstance
        return this.controller.firstOrNull { it.javaClass == controller } as? T
    }

    override fun registerController(controller: Controller): Boolean {
        this as DefaultEntityInstance
        // 添加事件
        if (AdyeshachControllerAddEvent(this, controller).call()) {
            unregisterController(controller::class.java)
            this.controller.add(controller)
            return true
        }
        return false
    }

    override fun <T : Controller> unregisterController(controller: Class<T>): Boolean {
        this as DefaultEntityInstance
        val element = this.controller.firstOrNull { it.javaClass == controller } ?: return true
        // 移除事件
        if (AdyeshachControllerRemoveEvent(this, element).call()) {
            element.onRemove()
            this.controller.remove(element)
            return true
        }
        return false
    }

    override fun resetController() {
        this as DefaultEntityInstance
        this.controller.forEach { unregisterController(it.javaClass) }
    }

    override fun isControllerMoving(): Boolean {
        this as DefaultEntityInstance
        return hasTag("isMoving")
    }

    override fun isControllerJumping(): Boolean {
        this as DefaultEntityInstance
        return hasTag("isJumping")
    }

    override fun isControllerOnGround(): Boolean {
        this as DefaultEntityInstance
        return getController(GeneralGravity::class.java)?.isOnGround ?: errorBy("error-no-gravity")
    }

    override fun controllerLook(location: Location, smooth: Boolean, smoothInternal: Float) {
        this as DefaultEntityInstance
        val size = Adyeshach.api().getEntityTypeHandler().getEntitySize(entityType)
        position.toLocation().add(0.0, size.height * 0.9, 0.0).also { entityLocation ->
            entityLocation.direction = location.clone().subtract(entityLocation).toVector()
            controllerLook(entityLocation.yaw, entityLocation.pitch, smooth, smoothInternal)
        }
    }

    override fun controllerLook(yaw: Float, pitch: Float, smooth: Boolean, smoothInternal: Float) {
        this as DefaultEntityInstance
        if (smooth && controller.any { it is GeneralSmoothLook }) {
            val look = getController(GeneralSmoothLook::class.java)!!
            look.yaw = yaw
            look.pitch = pitch
            look.isReset = true
            look.isLooking = true
            look.interval = smoothInternal
        } else {
            setHeadRotation(yaw, pitch)
        }
    }

    override fun controllerMove(location: Location, pathType: PathType, speed: Double) {
        this as DefaultEntityInstance
        // 在骑乘状态下无法寻路
        if (hasVehicle()) {
            return
        }
        if (pathType == PathType.FLY) {
            if (controller.none { it is GeneralMove }) {
                errorBy("error-no-move")
            }
        } else {
            if (controller.none { it is GeneralMove } || controller.none { it is GeneralGravity }) {
                errorBy("error-no-move-and-gravity")
            }
        }
        // 设置尝试移动的标签
        setTag("tryMoving", "true")
        // 请求寻路
        PathFinderHandler.request(position.toLocation(), location, pathType) {
            // 路径节点为空
            if ((it as ResultNavigation).pointList.isEmpty()) {
                return@request
            }
            val move = getController(GeneralMove::class.java)!!
            move.speed = speed
            move.target = location
            move.pathType = pathType
            move.resultNavigation = it
            // 移除标签
            removeTag("tryMoving")
        }
    }

    override fun controllerStill() {
        this as DefaultEntityInstance
        if (controller.any { it is GeneralMove }) {
            // 刷新 GeneralMove 控制器
            controller.removeIf { it is GeneralMove }
            controller.add(GeneralMove(this))
            // 移除标签
            removeTag("isMoving")
            removeTag("isJumping")
        }
    }

    override fun isTryMoving(): Boolean {
        this as DefaultEntityInstance
        return hasTag("tryMoving")
    }
}