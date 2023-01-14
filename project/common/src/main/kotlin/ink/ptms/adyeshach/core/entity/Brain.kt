package ink.ptms.adyeshach.core.entity

import ink.ptms.adyeshach.core.entity.controller.Controller
import java.util.Random

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.Brain
 *
 * @author 坏黑
 * @since 2023/1/14 23:29
 */
interface Brain {

    /** 执行控制器逻辑 */
    fun tick()

    /** 获取随机数生成器 */
    fun getRandom(): Random

    /** 获取正在运行的控制器 */
    fun getRunningControllers(): MutableMap<String, Controller>
}