package ink.ptms.adyeshach.impl.nms

import ink.ptms.adyeshach.common.api.MinecraftMeta

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.DefaultMeta
 *
 * @author 坏黑
 * @since 2022/6/30 16:31
 */
class DefaultMeta(val meta: Any): MinecraftMeta {

    override fun source(): Any {
        return meta
    }
}