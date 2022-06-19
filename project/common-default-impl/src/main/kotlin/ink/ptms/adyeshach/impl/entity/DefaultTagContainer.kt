package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.api.event.AdyeshachPersistentTagUpdateEvent
import ink.ptms.adyeshach.api.event.AdyeshachTagUpdateEvent
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.TagContainer

/**
 * Adyeshach
 * ink.ptms.adyeshach.internal.entity.DefaultTagContainer
 *
 * @author 坏黑
 * @since 2022/6/19 16:11
 */
interface DefaultTagContainer : TagContainer {

    override fun getTags(): Set<Map.Entry<String, String>> {
        this as DefaultEntityBase
        return tag.entries
    }

    override fun getTag(key: String): String? {
        this as DefaultEntityBase
        return tag[key]
    }

    override fun hasTag(key: String): Boolean {
        this as DefaultEntityBase
        return tag.containsKey(key)
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