package ink.ptms.adyeshach.core.entity

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.TagContainer
 *
 * @author 坏黑
 * @since 2022/6/16 00:05
 */
@Suppress("SpellCheckingInspection")
interface TagContainer {

    /**
     * 获取所有标签
     */
    fun getTags(): Set<Map.Entry<String, String>>

    /**
     * 获取标签
     */
    fun getTag(key: String): String?

    /**
     * 是否持有标签
     */
    fun hasTag(key: String): Boolean

    /**
     * 设置标签
     */
    fun setTag(key: String, value: String)

    /**
     * 移除标签
     */
    fun removeTag(key: String)

    /**
     * 获取所有持久化标签
     */
    fun getPersistentTags(): Set<Map.Entry<String, String>>

    /**
     * 获取持久化标签
     */
    fun getPersistentTag(key: String): String?

    /**
     * 是否持有持久化标签
     */
    fun hasPersistentTag(key: String): Boolean

    /**
     * 设置持久化标签
     */
    fun setPersistentTag(key: String, value: String)

    /**
     * 移除持久化标签
     */
    fun removePersistentTag(key: String)
}