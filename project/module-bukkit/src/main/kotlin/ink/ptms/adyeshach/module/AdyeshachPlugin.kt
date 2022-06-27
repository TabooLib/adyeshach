package ink.ptms.adyeshach.module

import ink.ptms.adyeshach.impl.DefaultAdyeshachBooster
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.disablePlugin

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.AdyeshachPlugin
 *
 * @author 坏黑
 * @since 2022/6/18 23:50
 */
object AdyeshachPlugin : Plugin() {

    override fun onLoad() {
        try {
            DefaultAdyeshachBooster().startup()
        } catch (ex: Throwable) {
            ex.printStackTrace()
            disablePlugin()
        }
    }
}