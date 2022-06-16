package ink.ptms.adyeshach.common.bukkit.nms

import ink.ptms.adyeshach.common.bukkit.nms.AdyeshachNMS
import ink.ptms.adyeshach.common.bukkit.nms.MinecraftMeta
import taboolib.common5.Coerce

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.bukkit.nms.MinecraftMetadataParser
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

    protected fun Any.toInt() = Coerce.toInteger(this)

    protected fun Any.toByte() = Coerce.toByte(this)

    protected fun Any.toDouble() = Coerce.toDouble(this)

    protected fun metadataHandler() = AdyeshachNMS.getEntityMetadataHandler()
}