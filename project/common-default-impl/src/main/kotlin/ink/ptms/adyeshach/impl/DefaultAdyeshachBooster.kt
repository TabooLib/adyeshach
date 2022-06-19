package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.common.api.Adyeshach

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.DefaultAdyeshachBooster
 *
 * @author 坏黑
 * @since 2022/6/19 17:12
 */
object DefaultAdyeshachBooster {

    /**
     * 启动 Adyeshach 服务
     */
    fun startup() {
        Adyeshach.register(DefaultAdyeshachAPI())
    }
}