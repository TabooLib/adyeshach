package ink.ptms.adyeshach.core.serializer

/**
 * 装备序列化异常
 * 
 * 当装备序列化或反序列化失败时抛出此异常。
 * 该异常包含详细的上下文信息，便于调试和错误处理。
 *
 * @author sky
 * @since 2025/12/26
 */
class EquipmentSerializationException(message: String, cause: Throwable? = null) : RuntimeException(message, cause) {

    constructor(message: String, serializer: String?, cause: Throwable? = null) : this("$message (serializer: $serializer)", cause)
}