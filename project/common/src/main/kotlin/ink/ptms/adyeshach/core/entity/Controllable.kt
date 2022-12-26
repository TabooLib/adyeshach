package ink.ptms.adyeshach.core.entity

import ink.ptms.adyeshach.core.entity.controller.Controller

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
}