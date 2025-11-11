package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.core.entity.TagContainer
import ink.ptms.adyeshach.core.event.AdyeshachPersistentTagUpdateEvent
import ink.ptms.adyeshach.core.event.AdyeshachTagUpdateEvent

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.DefaultTagContainer
 *
 * @author 坏黑
 * @since 2022/6/19 16:11
 */
interface DefaultTagContainer : TagContainer {

    override fun getTags(): Set<Map.Entry<String, Any>> {
        return collectTagMap().entries
    }

    override fun getTag(key: String): Any? {
        return collectTagMap()[key]
    }

    override fun hasTag(key: String): Boolean {
        return collectTagMap().containsKey(key)
    }

    override fun setTag(key: String, value: Any?) {
        this as DefaultEntityInstance
        val self = tag[key]
        if (self == value) {
            return
        }
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
        setTag(key, null)
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

    override fun setPersistentTag(key: String, value: String?) {
        this as DefaultEntityInstance
        val self = persistentTag[key]
        if (self == value) {
            return
        }
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
        setPersistentTag(key, null)
    }
}

/**
 * 整理实体标签
 */
fun DefaultTagContainer.collectTagMap(): Map<String, Any> {
    this as DefaultEntityInstance
    val tags = HashMap<String, Any>(tag)
    tags.putAll(persistentTag)
    return tags
}