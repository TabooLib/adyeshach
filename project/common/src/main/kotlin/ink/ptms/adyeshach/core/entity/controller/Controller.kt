package ink.ptms.adyeshach.core.entity.controller

import ink.ptms.adyeshach.core.entity.EntityInstance

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.ai.Controller
 *
 * @author 坏黑
 * @since 2022/6/15 23:38
 */
abstract class Controller(var entity: EntityInstance? = null) : Comparable<Controller> {

    var error = false

    /**
     * 控制器唯一序号
     */
    abstract fun id(): String

    /**
     * 控制器组
     */
    open fun group() = "default"

    /**
     * 优先级
     */
    open fun priority() = 0

    /**
     * 是否开始执行
     */
    abstract fun shouldExecute(): Boolean

    /**
     * 是否继续执行
     */
    open fun continueExecute(): Boolean {
        return shouldExecute()
    }

    /**
     * 开始时
     */
    open fun start() = Unit

    /**
     * 结束时
     */
    open fun stop() = Unit

    /**
     * 更新时
     */
    open fun tick() = Unit

    /**
     * 调试数据
     */
    open fun status(): String? = null

    /**
     * 是否异步运行
     */
    open fun isAsync() = false

    /**
     * 是否能被其他控制器中断
     */
    open fun isInterruptable() = true

    override fun compareTo(other: Controller): Int {
        return priority().compareTo(other.priority())
    }

    override fun toString(): String {
        return id()
    }

    protected fun adjustedTickDelay(tick: Int): Int {
        return -Math.floorDiv(-tick, 2);
    }
}