package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.core.entity.controller.Controller
import ink.ptms.adyeshach.core.entity.controller.PrepareController
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.SimpleBrain
 *
 * @author 坏黑
 * @since 2022/12/13 20:33
 */
open class SimpleBrain(val entity: DefaultEntityInstance) {

    /** 执行 */
    val hold = hashMapOf<String, Controller>()
    /** 打断 */
    val interrupt = hashMapOf<String, Controller>()
    /** 预加载控制器 */
    val postponeAdd = arrayListOf<Controller>()
    /** 每 30 秒检查一次 hold 内的无效控制器 */
    var checker = System.currentTimeMillis()
    /** 随机生成器 */
    val random = Random()

    fun tick() {
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
        // 本次是否检查无效控制器
        val checkInvalid = checker + TimeUnit.SECONDS.toMillis(30) < System.currentTimeMillis()
        if (checkInvalid) {
            checker = System.currentTimeMillis()
        }
        // 处理继续
        hold.forEach { (k, controller) ->
            // 检查无效控制器
            if (checkInvalid && !entity.controller.contains(controller)) {
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
            entity.controller.sort()
        }
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