package ink.ptms.adyeshach.core

import ink.ptms.adyeshach.core.entity.controller.ControllerGenerator

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.AdyeshachEntityControllerHandler
 *
 * @author 坏黑
 * @since 2022/6/16 17:32
 */
interface AdyeshachEntityControllerRegistry {

    /**
     * 注册 Controller 生成器
     */
    fun registerControllerGenerator(name: String, generator: ControllerGenerator)

    /**
     * 注销 Controller 生成器
     */
    fun unregisterControllerGenerator(name: String)

    /**
     * 获取 Controller 生成器
     */
    fun getControllerGenerator(name: String): ControllerGenerator?

    /**
     * 获取所有 Controller 生成器（副本）
     */
    fun getControllerGenerator(): Map<String, ControllerGenerator>
}