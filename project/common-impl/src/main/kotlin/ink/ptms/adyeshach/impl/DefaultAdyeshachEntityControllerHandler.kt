package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.core.AdyeshachEntityControllerRegistry
import ink.ptms.adyeshach.core.entity.controller.ControllerGenerator
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.PlatformFactory

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.DefaultAdyeshachEntityControllerHandler
 *
 * @author 坏黑
 * @since 2022/6/20 01:36
 */
class DefaultAdyeshachEntityControllerHandler : AdyeshachEntityControllerRegistry {

    override fun registerControllerGenerator(name: String, event: ControllerGenerator) {
        registeredControllerGenerator[name] = event
    }

    override fun getControllerGenerator(name: String): ControllerGenerator? {
        return registeredControllerGenerator.entries.firstOrNull { it.key.equals(name, true) }?.value
    }

    override fun getControllerGenerator(): Map<String, ControllerGenerator> {
        return registeredControllerGenerator.toMap()
    }

    companion object {

        val registeredControllerGenerator = LinkedHashMap<String, ControllerGenerator>()

        @Awake(LifeCycle.LOAD)
        fun init() {
            PlatformFactory.registerAPI<AdyeshachEntityControllerRegistry>(DefaultAdyeshachEntityControllerHandler())
        }
    }
}