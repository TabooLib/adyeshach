package ink.ptms.adyeshach.common.entity

import ink.ptms.adyeshach.common.entity.ai.Controller
import ink.ptms.adyeshach.common.entity.path.PathType
import org.bukkit.Location

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.entity.Controllable
 *
 * @author 坏黑
 * @since 2022/6/15 23:54
 */
interface Controllable {

    /**
     * 是否冻结实体，启用后无法移动
     */
    var isFreeze: Boolean

    /**
     * 移动速度，用于巡逻特性
     */
    var moveSpeed: Double

    /**
     * 获取所有控制器
     */
    fun getController(): List<Controller>

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
    fun <T : Controller> unregisterController(controller: Class<T>): Boolean

    /**
     * 重置控制器
     */
    fun resetController()

    /**
     * 单位是否在移动状态
     */
    fun isControllerMoving(): Boolean

    /**
     * 单位是否在跳跃状态
     */
    fun isControllerJumping(): Boolean

    /**
     * 单位是否在地表（依赖 Gravity 控制器）
     */
    fun isControllerOnGround(): Boolean

    /**
     * 使实体看向某个坐标
     */
    fun controllerLook(location: Location, smooth: Boolean = false, smoothInternal: Float = 22.5f)

    /**
     * 使实体看向某个视角
     */
    fun controllerLook(yaw: Float, pitch: Float, smooth: Boolean = false, smoothInternal: Float = 22.5f)

    /**
     * 使实体移动到某个坐标
     */
    fun controllerMove(location: Location, pathType: PathType, speed: Double)

    /**
     * 使实体停止移动
     */
    fun controllerStill()

    /**
     * 单位是否在尝试移动（等待寻路的过程）
     */
    fun isTryMoving(): Boolean
}