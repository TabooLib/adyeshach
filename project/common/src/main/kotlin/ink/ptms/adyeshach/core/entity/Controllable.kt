package ink.ptms.adyeshach.core.entity

import ink.ptms.adyeshach.core.entity.controller.Controller
import org.bukkit.Location
import org.bukkit.entity.Entity
import java.util.*

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.Controllable
 *
 * @author 坏黑
 * @since 2022/6/15 23:54
 */
interface Controllable {

    /**
     * 是否冻结单位，启用后无法移动，但可以被传送
     */
    var isFreeze: Boolean

    /**
     * 单位移动速度
     */
    var moveSpeed: Double

    /**
     * 获取所有控制器
     */
    fun getController(): List<Controller>

    /**
     * 获取控制器
     */
    fun <T : Controller> getController(controller: String): T?

    /**
     * 获取控制器
     */
    fun <T : Controller> getController(controller: Class<T>): T?

    /**
     * 注册控制器
     */
    fun registerController(controller: Controller): Boolean

    /**
     * 注销控制器
     */
    fun unregisterController(controller: String): Boolean

    /**
     * 注销控制器
     */
    fun unregisterController(controller: Controller): Boolean

    /**
     * 注销控制器
     */
    fun <T : Controller> unregisterController(controller: Class<T>): Boolean

    /**
     * 重置控制器
     */
    fun resetController()

    /**
     * 看向某个实体（不会立刻看向目标，类似原版的阶段性视角移动）
     */
    fun controllerLookAt(entity: Entity)

    /**
     * 看向某个实体（不会立刻看向目标，类似原版的阶段性视角移动）
     */
    fun controllerLookAt(entity: Entity, yMaxRotSpeed: Float, xMaxRotAngle: Float)

    /**
     * 看向某个位置（不会立刻看向目标，类似原版的阶段性视角移动）
     */
    fun controllerLookAt(wantedX: Double, wantedY: Double, wantedZ: Double)

    /**
     * 看向某个位置（不会立刻看向目标，类似原版的阶段性视角移动）
     */
    fun controllerLookAt(wantedX: Double, wantedY: Double, wantedZ: Double, yMaxRotSpeed: Float)

    /**
     * 看向某个位置（不会立刻看向目标，类似原版的阶段性视角移动）
     */
    fun controllerLookAt(wantedX: Double, wantedY: Double, wantedZ: Double, yMaxRotSpeed: Float, xMaxRotAngle: Float)

    /**
     * 移动到某个位置
     */
    fun controllerMoveTo(location: Location)

    /**
     * 按照规定的路径移动
     */
    fun controllerMoveBy(locations: List<Location>, speed: Double = moveSpeed, fixHeight: Boolean = true)

    /**
     * 停止移动
     */
    fun controllerStopMove()

    /**
     * 获取随机数生成器
     */
    fun random(): Random
}