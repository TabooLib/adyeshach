package ink.ptms.adyeshach.module.bukkit

import ink.ptms.adyeshach.impl.DefaultAdyeshachBooster
import taboolib.common.LifeCycle
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.disablePlugin
import taboolib.common.platform.function.registerLifeCycleTask

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.bukkit.AdyeshachPlugin
 *
 * @author 坏黑
 * @since 2022/6/18 23:50
 */
object AdyeshachPlugin : Plugin() {

    init {
        registerLifeCycleTask(LifeCycle.INIT) {
            try {
                DefaultAdyeshachBooster.startup()
            } catch (ex: Throwable) {
                ex.printStackTrace()
                disablePlugin()
            }
        }
    }
}