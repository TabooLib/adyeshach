package ink.ptms.adyeshach.impl.entity.handler

import ink.ptms.adyeshach.core.entity.Controllable
import ink.ptms.adyeshach.core.entity.StandardTags
import ink.ptms.adyeshach.core.entity.controller.Controller
import ink.ptms.adyeshach.core.entity.path.ResultNavigation
import ink.ptms.adyeshach.core.event.AdyeshachControllerAddEvent
import ink.ptms.adyeshach.core.event.AdyeshachControllerRemoveEvent
import ink.ptms.adyeshach.impl.entity.DefaultEntityInstance
import ink.ptms.adyeshach.impl.util.ChunkAccess
import org.bukkit.Location
import java.util.*

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.handler.ControllerHandler
 *
 * 负责实体的控制器相关逻辑
 */
@Suppress("UNCHECKED_CAST")
open class ControllerHandler(protected val self: DefaultEntityInstance) : Controllable {

    override var isFreeze: Boolean
        set(value) {
            self.setTag(StandardTags.IS_FROZEN, if (value) "true" else null)
        }
        get() = self.hasTag(StandardTags.IS_FROZEN)

    override var moveSpeed: Double
        get() = self.moveSpeed
        set(value) { self.moveSpeed = value }

    override fun getController(): List<Controller> {
        return self.controller.toList()
    }

    override fun <T : Controller> getController(controller: String): T? {
        return self.controller.firstOrNull { it.id() == controller } as? T
    }

    override fun <T : Controller> getController(controller: Class<T>): T? {
        return self.controller.firstOrNull { it.javaClass == controller } as? T
    }

    override fun registerController(controller: Controller): Boolean {
        // 添加事件
        if (AdyeshachControllerAddEvent(self, controller).call()) {
            // 移除相同的控制器
            self.controller.removeIf { it.id() == controller.id() }
            self.controller.add(controller)
            return true
        }
        return false
    }

    override fun unregisterController(controller: String): Boolean {
        return unregister(self.controller.firstOrNull { it.id() == controller } ?: return true)
    }

    override fun unregisterController(controller: Controller): Boolean {
        return unregister(self.controller.firstOrNull { it == controller } ?: return true)
    }

    override fun <T : Controller> unregisterController(controller: Class<T>): Boolean {
        return unregister(self.controller.firstOrNull { it.javaClass == controller } ?: return true)
    }

    override fun resetController() {
        self.controller.forEach { unregisterController(it.javaClass) }
    }

    override fun controllerLookAt(entity: org.bukkit.entity.Entity) {
        self.bionicSight.setLookAt(entity)
    }

    override fun controllerLookAt(entity: org.bukkit.entity.Entity, yMaxRotSpeed: Float, xMaxRotAngle: Float) {
        self.bionicSight.setLookAt(entity, yMaxRotSpeed, xMaxRotAngle)
    }

    override fun controllerLookAt(wantedX: Double, wantedY: Double, wantedZ: Double) {
        self.bionicSight.setLookAt(wantedX, wantedY, wantedZ)
    }

    override fun controllerLookAt(wantedX: Double, wantedY: Double, wantedZ: Double, yMaxRotSpeed: Float) {
        self.bionicSight.setLookAt(wantedX, wantedY, wantedZ, yMaxRotSpeed, 40f)
    }

    override fun controllerLookAt(wantedX: Double, wantedY: Double, wantedZ: Double, yMaxRotSpeed: Float, xMaxRotAngle: Float) {
        self.bionicSight.setLookAt(wantedX, wantedY, wantedZ, yMaxRotSpeed, xMaxRotAngle)
    }

    override fun controllerMoveTo(location: Location) {
        self.moveTarget = location
    }

    override fun controllerMoveBy(locations: List<Location>, speed: Double, fixHeight: Boolean) {
        if (locations.isEmpty()) {
            return
        }
        // 克隆坐标列表
        val newList = locations.map { it.clone() }
        // 修正路径高度
        if (fixHeight) {
            newList.forEach { p -> p.y = ChunkAccess.getChunkAccess(self.world).getBlockHighest(p.x, p.y, p.z) }
        }
        // 设置移动路径
        self.moveFrames = ResultNavigation(newList.map { it.toVector() }.toMutableList()).toInterpolated(self.world, speed)
    }

    override fun controllerStopMove() {
        self.moveFrames = null
    }

    override fun random(): Random {
        return self.brain.getRandom()
    }

    private fun unregister(controller: Controller): Boolean {
        // 移除事件
        if (AdyeshachControllerRemoveEvent(self, controller).call()) {
            self.controller.remove(controller)
            // 从执行容器中移除
            if (self.brain.getRunningControllers()[controller.group()] == controller) {
                self.brain.getRunningControllers().remove(controller.group())
            }
            return true
        }
        return false
    }
}
