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
    fun getTags(): Set<Map.Entry<String, Any>>

    /**
     * 获取标签
     */
    fun getTag(key: String): Any?

    /**
     * 是否持有标签
     */
    fun hasTag(key: String): Boolean

    /**
     * 是否持有标签
     */
    fun hasTag(vararg key: String): Boolean = key.any { hasTag(it) }

    /**
     * 设置标签
     */
    fun setTag(key: String, value: Any?)

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
    fun setPersistentTag(key: String, value: String?)

    /**
     * 移除持久化标签
     */
    fun removePersistentTag(key: String)

    /**
     * 添加衍生物标签并设置为傻子
     */
    fun setDerived(id: String)

    /**
     * 是否为衍生物
     */
    fun isDerived(): Boolean {
        return hasPersistentTag(StandardTags.DERIVED)
    }

    /**
     * 是否不可编辑
     * 不会触发 /npc edit 快速编辑
     */
    fun isUneditable(): Boolean {
        return hasPersistentTag(StandardTags.UNEDITABLE)
    }
}