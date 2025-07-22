package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.core.entity.Brain
import ink.ptms.adyeshach.core.entity.controller.Controller
import ink.ptms.adyeshach.core.entity.controller.PrepareController
import taboolib.common.platform.function.info
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.SimpleBrain
 *
 * @author 坏黑
 * @since 2022/12/13 20:33
 */
open class SimpleBrain(val entity: DefaultEntityInstance) : Brain {

    /** 执行容器 */
    protected val hold = ConcurrentHashMap<String, Controller>()
    /** 随机生成器 */
    protected val rand = Random()

    /** 打断 */
    protected val interrupt = hashMapOf<String, Controller>()
    /** 预加载控制器 */
    protected val postponeAdd = arrayListOf<Controller>()

    override fun tick() {
        interrupt.clear()
        postponeAdd.clear()
        // 检查所有控制器
        entity.controller.forEach { controller ->
            // 是否为预加载控制器
            if (controller is PrepareController) {
                postponeAdd += controller.generator.generate(entity)
                entity.controller.remove(controller)
                return@forEach
            }
            // 获取当前正在运行的同组控制器
            val h = hold[controller.group()]
            // 当前正在运行
            if (h == controller) {
                // 无法继续运行
                if (controller.runSafely(true) { !continueExecute() }) {
                    // 停止
                    hold.remove(controller.group())
                    controller.stop()
                }
            }
            // 不在运行或是能够被打断
            // 能被打断的前提是优先级高于正在运行的控制器（数字较小）
            else if (h == null || (controller < h && h.isInterruptable())) {
                // 是否需要开始运行
                if (controller.runSafely(false) { shouldExecute() }) {
                    // 开始
                    interrupt[controller.group()] = controller
                }
            }
        }
        // 处理中断
        interrupt.forEach { (k, controller) ->
            // 停止
            hold[k]?.stop()
            // 开始
            hold[k] = controller.also { it.start() }
        }
        // 处理继续
        hold.forEach { (k, controller) ->
            // 检查无效控制器
            if (!entity.controller.contains(controller)) {
                hold.remove(k)
            }
            // 不是被中断的
            if (!interrupt.containsKey(k)) {
                // 执行
                if (controller.isAsync()) {
                    pool.submit { controller.runSafely(Unit) { tick() } }
                } else {
                    controller.runSafely(Unit) { tick() }
                }
            }
        }
        // 处理预加载控制器
        if (postponeAdd.isNotEmpty()) {
            entity.controller.addAll(postponeAdd)
        }
    }

    override fun getRandom(): Random {
        return rand
    }

    override fun getRunningControllers(): MutableMap<String, Controller> {
        return hold
    }

    companion object {

        private val pool = Executors.newFixedThreadPool(16)!!

        private inline fun <T> Controller.runSafely(def: T, func: Controller.() -> T): T {
            if (error) {
                return def
            }
            return try {
                func(this)
            } catch (ex: Throwable) {
                ex.printStackTrace()
                error = true
                def
            }
        }
    }
}