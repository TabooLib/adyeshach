package ink.ptms.adyeshach.core

import taboolib.common5.Coerce

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.MinecraftMetadataParser
 *
 * @author 坏黑
 * @since 2022/6/15 18:10
 */
abstract class MinecraftMetadataParser<T> {

    /**
     * 解析对象
     */
    abstract fun parse(value: Any): T

    /**
     * 创建为 [MinecraftMeta] 类型
     */
    abstract fun createMeta(index: Int, value: T): MinecraftMeta

    protected fun metadataHandler() = Adyeshach.api().getMinecraftAPI().getEntityMetadataHandler()
}