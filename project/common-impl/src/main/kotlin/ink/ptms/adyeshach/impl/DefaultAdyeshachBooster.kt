package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.core.Adyeshach
import taboolib.common.util.unsafeLazy

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.DefaultAdyeshachBooster
 *
 * @author 坏黑
 * @since 2022/6/19 17:12
 */
object DefaultAdyeshachBooster {

    val api by unsafeLazy { DefaultAdyeshachAPI() }

    /**
     * 启动 Adyeshach 服务
     */
    fun startup() {
        Adyeshach.register(api)
    }
}