package ink.ptms.adyeshach.internal

import ink.ptms.adyeshach.common.api.Adyeshach
import taboolib.common.platform.Plugin

/**
 * Adyeshach
 * ink.ptms.adyeshach.internal.AdyeshachPlugin
 *
 * @author 坏黑
 * @since 2022/6/18 23:50
 */
object AdyeshachPlugin : Plugin() {

    override fun onLoad() {
        Adyeshach.register(DefaultAdyeshachAPI())
    }
}