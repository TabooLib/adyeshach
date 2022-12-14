package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.api.event.AdyeshachControllerAddEvent
import ink.ptms.adyeshach.api.event.AdyeshachControllerRemoveEvent
import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.entity.Controllable
import ink.ptms.adyeshach.common.entity.StandardTags
import ink.ptms.adyeshach.common.entity.TagContainer
import ink.ptms.adyeshach.common.entity.ai.Controller
import ink.ptms.adyeshach.common.entity.path.PathType
import ink.ptms.adyeshach.common.util.path.PathFinderHandler
import ink.ptms.adyeshach.common.util.path.ResultNavigation
import org.bukkit.Location

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.DefaultControllable
 *
 * @author 坏黑
 * @since 2022/6/19 21:57
 */
@Suppress("UNCHECKED_CAST")
interface DefaultControllable : Controllable {

    override var isFreeze: Boolean
        set(value) {
            this as TagContainer
            setTag(StandardTags.IS_FREEZE, value.toString())
        }
        get() {
            this as TagContainer
            return hasTag(StandardTags.IS_FREEZE)
        }

    override fun getController(): List<Controller> {
        this as DefaultEntityInstance
        return controller.toList()
    }

    override fun <T : Controller> getController(controller: String): T? {
        this as DefaultEntityInstance
        return this.controller.firstOrNull { it.id() == controller } as? T
    }

    override fun <T : Controller> getController(controller: Class<T>): T? {
        this as DefaultEntityInstance
        return this.controller.firstOrNull { it.javaClass == controller } as? T
    }

    override fun registerController(controller: Controller): Boolean {
        this as DefaultEntityInstance
        // 添加事件
        if (AdyeshachControllerAddEvent(this, controller).call()) {
            // 移除相同的控制器
            this.controller.removeIf { it.id() == controller.id() }
            // 注册控制器
            this.controller.add(controller)
            // 排序
            this.controller.sort()
            return true
        }
        return false
    }

    override fun unregisterController(controller: String): Boolean {
        this as DefaultEntityInstance
        return unregister(this.controller.firstOrNull { it.id() == controller } ?: return true)
    }

    override fun unregisterController(controller: Controller): Boolean {
        this as DefaultEntityInstance
        return unregister(this.controller.firstOrNull { it == controller } ?: return true)
    }

    override fun <T : Controller> unregisterController(controller: Class<T>): Boolean {
        this as DefaultEntityInstance
        return unregister(this.controller.firstOrNull { it.javaClass == controller } ?: return true)
    }

    override fun resetController() {
        this as DefaultEntityInstance
        this.controller.forEach { unregisterController(it.javaClass) }
    }

    override fun isControllerMoving(): Boolean {
        this as DefaultEntityInstance
        return hasTag(StandardTags.IS_MOVING)
    }

    override fun isControllerJumping(): Boolean {
        this as DefaultEntityInstance
        return hasTag(StandardTags.IS_JUMPING)
    }

    override fun isControllerOnGround(): Boolean {
        this as DefaultEntityInstance
        return hasTag(StandardTags.IS_ON_GROUND)
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
//        TODO 重做
//        if (smooth && controller.any { it is GeneralSmoothLook }) {
//            val look = getController(GeneralSmoothLook::class.java)!!
//            look.yaw = yaw
//            look.pitch = pitch
//            look.isReset = true
//            look.isLooking = true
//            look.interval = smoothInternal
//        } else {
        sendHeadRotation(yaw, pitch)
//        }
    }

    override fun controllerMove(location: Location, pathType: PathType, speed: Double) {
        this as DefaultEntityInstance
        // 在骑乘状态下无法寻路
        if (hasVehicle()) {
            return
        }
//        TODO 重做
//        // 飞行单位需要 Move 控制器
//        if (pathType == PathType.FLY && controller.none { it is GeneralMove }) {
//            errorBy("error-no-move")
//        }
//        // 移动单位需要 Move、Gravity 控制器
//        if (controller.none { it is GeneralMove } || controller.none { it is GeneralGravity }) {
//            errorBy("error-no-move-and-gravity")
//        }
        // 设置尝试移动的标签
        setTag(StandardTags.IS_PATHFINDING, "true")
        // 请求寻路
        PathFinderHandler.request(position.toLocation(), location, pathType) {
            // 路径节点为空
            if ((it as ResultNavigation).pointList.isEmpty()) {
                return@request
            }
//            val move = getController(GeneralMove::class.java)!!
//            move.speed = speed
//            move.target = location
//            move.pathType = pathType
//            move.resultNavigation = it
            // 移除标签
            removeTag(StandardTags.IS_PATHFINDING)
        }
    }

    override fun controllerStill() {
        this as DefaultEntityInstance
//        // TODO 重做
//        if (controller.any { it is GeneralMove }) {
//            // 刷新 GeneralMove 控制器
//            controller.removeIf { it is GeneralMove }
//            controller.add(GeneralMove(this))
//            controller.sort()
//            // 移除标签
//            removeTag(StandardTags.IS_MOVING)
//            removeTag(StandardTags.IS_JUMPING)
//        }
    }

    override fun isTryMoving(): Boolean {
        this as DefaultEntityInstance
        return hasTag(StandardTags.IS_PATHFINDING)
    }

    private fun unregister(controller: Controller): Boolean {
        this as DefaultEntityInstance
        // 移除事件
        if (AdyeshachControllerRemoveEvent(this, controller).call()) {
            this.controller.remove(controller)
            return true
        }
        return false
    }
}