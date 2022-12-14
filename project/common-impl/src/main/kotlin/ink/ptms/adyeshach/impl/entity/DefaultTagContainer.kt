package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.core.event.AdyeshachPersistentTagUpdateEvent
import ink.ptms.adyeshach.core.event.AdyeshachTagUpdateEvent
import ink.ptms.adyeshach.core.entity.StandardTags
import ink.ptms.adyeshach.core.entity.TagContainer

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.DefaultTagContainer
 *
 * @author 坏黑
 * @since 2022/6/19 16:11
 */
interface DefaultTagContainer : TagContainer {

    override fun getTags(): Set<Map.Entry<String, String>> {
        this as DefaultEntityInstance
        val tags = HashMap<String, String>()
        // 修正标签
        val manager = this.manager
        if (manager == null) {
            tags[StandardTags.ISOLATED] = "true"
        } else {
            if (manager.isPublic()) {
                tags[StandardTags.IS_PUBLIC] = "true"
            } else {
                tags[StandardTags.IS_PRIVATE] = "true"
            }
            if (manager.isTemporary()) {
                tags[StandardTags.IS_TEMPORARY] = "true"
            }
        }
        val isDerived = hasPersistentTag(StandardTags.DERIVED)
        if (isDerived) {
            tags[StandardTags.DERIVED] = "true"
        }
        tags.putAll(tag)
        return tags.entries
    }

    override fun getTag(key: String): String? {
        this as DefaultEntityBase
        return getTags().firstOrNull { it.key == key }?.value
    }

    override fun hasTag(key: String): Boolean {
        this as DefaultEntityBase
        return getTags().any { it.key == key }
    }

    override fun setTag(key: String, value: String) {
        this as DefaultEntityInstance
        val event = AdyeshachTagUpdateEvent(this, key, value)
        if (event.call()) {
            if (event.value != null) {
                tag[key] = event.value!!
            } else {
                tag.remove(key)
            }
        }
    }

    override fun removeTag(key: String) {
        this as DefaultEntityInstance
        val event = AdyeshachTagUpdateEvent(this, key, null)
        if (event.call()) {
            if (event.value != null) {
                tag[key] = event.value!!
            } else {
                tag.remove(key)
            }
        }
    }

    override fun getPersistentTags(): Set<Map.Entry<String, String>> {
        this as DefaultEntityBase
        return persistentTag.entries
    }

    override fun getPersistentTag(key: String): String? {
        this as DefaultEntityBase
        return persistentTag[key]
    }

    override fun hasPersistentTag(key: String): Boolean {
        this as DefaultEntityBase
        return persistentTag.containsKey(key)
    }

    override fun setPersistentTag(key: String, value: String) {
        this as DefaultEntityInstance
        val event = AdyeshachPersistentTagUpdateEvent(this, key, value)
        if (event.call()) {
            if (event.value != null) {
                persistentTag[key] = event.value!!
            } else {
                persistentTag.remove(key)
            }
        }
    }

    override fun removePersistentTag(key: String) {
        this as DefaultEntityInstance
        val event = AdyeshachPersistentTagUpdateEvent(this, key, null)
        if (event.call()) {
            if (event.value != null) {
                persistentTag[key] = event.value!!
            } else {
                persistentTag.remove(key)
            }
        }
    }
}