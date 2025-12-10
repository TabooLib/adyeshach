package ink.ptms.adyeshach.impl.entity.handler

import ink.ptms.adyeshach.core.event.AdyeshachPersistentTagUpdateEvent
import ink.ptms.adyeshach.core.event.AdyeshachTagUpdateEvent
import ink.ptms.adyeshach.impl.entity.DefaultEntityInstance

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.handler.TagHandler
 *
 * 负责实体标签的管理（临时标签和持久化标签）
 */
class TagHandler(private val self: DefaultEntityInstance) {

    // ═══════════════════════════════════════════════════════════════════════════════
    // 临时标签
    // ═══════════════════════════════════════════════════════════════════════════════

    /**
     * 获取所有标签（临时 + 持久化）
     */
    fun getTags(): Set<Map.Entry<String, Any>> {
        return collectTagMap().entries
    }

    /**
     * 获取标签
     */
    fun getTag(key: String): Any? {
        return collectTagMap()[key]
    }

    /**
     * 是否持有标签
     */
    fun hasTag(key: String): Boolean {
        return collectTagMap().containsKey(key)
    }

    /**
     * 设置标签
     */
    fun setTag(key: String, value: Any?) {
        val currentValue = self.tag[key]
        if (currentValue == value) {
            return
        }
        val event = AdyeshachTagUpdateEvent(self, key, value)
        if (event.call()) {
            if (event.value != null) {
                self.tag[key] = event.value!!
            } else {
                self.tag.remove(key)
            }
        }
    }

    /**
     * 移除标签
     */
    fun removeTag(key: String) {
        setTag(key, null)
    }

    // ═══════════════════════════════════════════════════════════════════════════════
    // 持久化标签
    // ═══════════════════════════════════════════════════════════════════════════════

    /**
     * 获取所有持久化标签
     */
    fun getPersistentTags(): Set<Map.Entry<String, String>> {
        return self.persistentTag.entries
    }

    /**
     * 获取持久化标签
     */
    fun getPersistentTag(key: String): String? {
        return self.persistentTag[key]
    }

    /**
     * 是否持有持久化标签
     */
    fun hasPersistentTag(key: String): Boolean {
        return self.persistentTag.containsKey(key)
    }

    /**
     * 设置持久化标签
     */
    fun setPersistentTag(key: String, value: String?) {
        val currentValue = self.persistentTag[key]
        if (currentValue == value) {
            return
        }
        val event = AdyeshachPersistentTagUpdateEvent(self, key, value)
        if (event.call()) {
            if (event.value != null) {
                self.persistentTag[key] = event.value!!
            } else {
                self.persistentTag.remove(key)
            }
        }
    }

    /**
     * 移除持久化标签
     */
    fun removePersistentTag(key: String) {
        setPersistentTag(key, null)
    }

    // ═══════════════════════════════════════════════════════════════════════════════
    // 内部方法
    // ═══════════════════════════════════════════════════════════════════════════════

    /**
     * 整理实体标签（合并临时标签和持久化标签）
     */
    private fun collectTagMap(): Map<String, Any> {
        val tags = HashMap<String, Any>(self.tag)
        tags.putAll(self.persistentTag)
        return tags
    }
}
