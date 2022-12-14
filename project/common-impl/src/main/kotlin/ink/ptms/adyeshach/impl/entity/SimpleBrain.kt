package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.common.entity.ai.Controller
import ink.ptms.adyeshach.common.entity.ai.PrepareController
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

    fun tick() {
        interrupt.clear()
        postponeAdd.clear()
        // 检查所有控制器
        entity.controller.forEach { controller ->
            // 是否为预加载控制器
            if (controller is PrepareController) {
                postponeAdd += controller.generator(entity)
                entity.controller.remove(controller)
                return@forEach
            }
            // 获取当前正在运行的同组控制器
            val h = hold[controller.key()]
            // 当前正在运行
            if (h == controller) {
                // 无法继续运行
                if (!controller.continueExecute()) {
                    // 停止
                    hold.remove(controller.key())
                    controller.stop()
                }
            }
            // 不在运行或是能够被打断
            // 能被打断的前提是优先级低于正在运行的控制器（数字较大）
            else if (h == null || (h > controller && h.isInterruptable())) {
                // 是否需要开始运行
                if (controller.shouldExecute()) {
                    // 开始
                    interrupt[controller.key()] = controller
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
                    pool.submit { controller.tick() }
                } else {
                    controller.tick()
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
    }
}