package ink.ptms.adyeshach.common.entity

import ink.ptms.adyeshach.impl.entity.DefaultMetaMasked
import ink.ptms.adyeshach.impl.entity.DefaultMetaNatural
import taboolib.common5.cbool
import taboolib.module.nms.MinecraftVersion
import java.util.concurrent.ConcurrentHashMap

@Deprecated("Outdated but usable")
abstract class EntityMetaable {

    open fun <T> getMetadata(key: String): T {
        this as EntityBase
        return v2.getMetadata(key)
    }

    open fun setMetadata(key: String, value: Any): Boolean {
        this as EntityBase
        return v2.setMetadata(key, value)
    }

    open fun getEditableEntityMeta(): List<Meta<*>> {
        this as EntityBase
        return emptyList()
    }

    open fun getAvailableEntityMeta(): List<Meta<*>> {
        this as EntityBase
        return v2.getAvailableEntityMeta().map {
            when (it) {
                is DefaultMetaNatural<*, *> -> MetaNatural<Any, Any>(it.index, it.key, it.def, it)
                is DefaultMetaMasked<*> -> MetaMasked<Any>(it.index, it.key, it.mask, it.def.cbool, it)
                else -> error("Not support: $it")
            }
        }
    }

    open fun getTags(): Set<Map.Entry<String, Any>> {
        this as EntityBase
        return v2.getTags()
    }

    open fun getTag(key: String): String? {
        this as EntityBase
        return v2.getTag(key)?.toString()
    }

    open fun getTagValue(key: String): Any? {
        this as EntityBase
        return v2.getTag(key)
    }

    open fun hasTag(key: String): Boolean {
        this as EntityBase
        return v2.hasTag(key)
    }

    open fun setTag(key: String, value: String) {
        this as EntityBase
        v2.setTag(key, value)
    }

    open fun setTag(key: String, value: Any) {
        this as EntityBase
        v2.setTag(key, value)
    }

    open fun removeTag(key: String) {
        this as EntityBase
        v2.removeTag(key)
    }

    open fun getPersistentTags(): Set<Map.Entry<String, String>> {
        this as EntityBase
        return v2.getPersistentTags()
    }

    open fun getPersistentTag(key: String): String? {
        this as EntityBase
        return v2.getPersistentTag(key)
    }

    open fun hasPersistentTag(key: String): Boolean {
        this as EntityBase
        return v2.hasPersistentTag(key)
    }

    open fun setPersistentTag(key: String, value: String) {
        this as EntityBase
        v2.setPersistentTag(key, value)
    }

    open fun removePersistentTag(key: String) {
        this as EntityBase
        v2.removePersistentTag(key)
    }

    companion object {

        val minecraftVersion = MinecraftVersion.majorLegacy
        val metaCache = ConcurrentHashMap<Class<*>, List<Meta<*>>>()
    }
}