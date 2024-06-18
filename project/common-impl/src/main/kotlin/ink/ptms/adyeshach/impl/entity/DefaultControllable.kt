package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.core.entity.Controllable
import ink.ptms.adyeshach.core.entity.StandardTags
import ink.ptms.adyeshach.core.entity.TagContainer
import ink.ptms.adyeshach.core.entity.controller.Controller
import ink.ptms.adyeshach.core.entity.path.ResultNavigation
import ink.ptms.adyeshach.core.event.AdyeshachControllerAddEvent
import ink.ptms.adyeshach.core.event.AdyeshachControllerRemoveEvent
import ink.ptms.adyeshach.impl.util.ChunkAccess
import org.bukkit.Location
import org.bukkit.entity.Entity
import java.util.*

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
            setTag(StandardTags.IS_FROZEN, if (value) "true" else null)
        }
        get() {
            this as TagContainer
            return hasTag(StandardTags.IS_FROZEN)
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

    override fun controllerLookAt(entity: Entity) {
        this as DefaultEntityInstance
        this.bionicSight.setLookAt(entity)
    }

    override fun controllerLookAt(entity: Entity, yMaxRotSpeed: Float, xMaxRotAngle: Float) {
        this as DefaultEntityInstance
        this.bionicSight.setLookAt(entity, yMaxRotSpeed, xMaxRotAngle)
    }

    override fun controllerLookAt(wantedX: Double, wantedY: Double, wantedZ: Double) {
        this as DefaultEntityInstance
        this.bionicSight.setLookAt(wantedX, wantedY, wantedZ)
    }

    override fun controllerLookAt(wantedX: Double, wantedY: Double, wantedZ: Double, yMaxRotSpeed: Float) {
        this as DefaultEntityInstance
        this.bionicSight.setLookAt(wantedX, wantedY, wantedZ, yMaxRotSpeed, 40f)
    }


    override fun controllerLookAt(wantedX: Double, wantedY: Double, wantedZ: Double, yMaxRotSpeed: Float, xMaxRotAngle: Float) {
        this as DefaultEntityInstance
        this.bionicSight.setLookAt(wantedX, wantedY, wantedZ, yMaxRotSpeed, xMaxRotAngle)
    }

    override fun controllerMoveTo(location: Location) {
        this as DefaultEntityInstance
        moveTarget = location
    }

    override fun controllerMoveBy(locations: List<Location>, speed: Double, fixHeight: Boolean) {
        if (locations.isEmpty()) {
            return
        }
        this as DefaultEntityInstance
        // 克隆坐标列表
        val newList = locations.map { it.clone() }
        // 修正路径高度
        if (fixHeight) {
             newList.forEach { p -> p.y = ChunkAccess.getChunkAccess(world).getBlockHighest(p.x, p.y, p.z) }
        }
        // 设置移动路径
        moveFrames = ResultNavigation(newList.map { it.toVector() }.toMutableList()).toInterpolated(world, speed)
    }

    override fun controllerStopMove() {
        this as DefaultEntityInstance
        moveFrames = null
    }

    override fun random(): Random {
        this as DefaultEntityInstance
        return this.brain.getRandom()
    }

    private fun unregister(controller: Controller): Boolean {
        this as DefaultEntityInstance
        // 移除事件
        if (AdyeshachControllerRemoveEvent(this, controller).call()) {
            this.controller.remove(controller)
            // 从执行容器中移除
            if (this.brain.getRunningControllers()[controller.group()] == controller) {
                this.brain.getRunningControllers().remove(controller.group())
            }
            return true
        }
        return false
    }
}