package ink.ptms.adyeshach.common.entity

import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.event.AdyeshachMetaUpdateEvent
import ink.ptms.adyeshach.api.event.AdyeshachTagUpdateEvent
import taboolib.common.io.digest
import taboolib.module.nms.MinecraftVersion
import java.util.concurrent.ConcurrentHashMap

@Suppress("UNCHECKED_CAST")
abstract class EntityMetaable {

    /**
     * 临时标签
     * 不会通过持久化
     */
    internal val tag = ConcurrentHashMap<String, String>()

    @Expose
    internal val metadata = ConcurrentHashMap<String, Any>()

    @Expose
    internal val metadataMask = ConcurrentHashMap<String, MutableMap<String, Boolean>>()

    /**
     * 获取实体元数据
     */
    open fun <T> getMetadata(key: String): T {
        val natural = getAvailableEntityMeta().firstOrNull { it.key == key } ?: error("Metadata \"$key\" not registered.")
        if (natural.index == -1) {
            error("Metadata \"$key\" not supported this minecraft version.")
        }
        return if (natural is MetaMasked) {
            (metadataMask[getByteMaskKey(natural.index)]?.get(key) ?: natural.def) as T
        } else {
            natural.dataWatcher.parse(metadata[key] ?: natural.def) as T
        }
    }

    /**
     * 设置实体元数据
     */
    open fun setMetadata(key: String, value: Any): Boolean {
        val natural = getAvailableEntityMeta().firstOrNull { it.key == key } ?: error("Metadata \"$key\" not registered.")
        if (natural.index == -1) {
            error("Metadata \"$key\" not supported this minecraft version.")
        }
        if (natural.index == -2) {
            error("Metadata \"$key\" not allowed.")
        }
        val event = AdyeshachMetaUpdateEvent(this, natural, key, value)
        if (event.call()) {
            if (natural is MetaMasked) {
                metadataMask.computeIfAbsent(getByteMaskKey(natural.index)) { ConcurrentHashMap() }[key] = event.value as Boolean
            } else {
                metadata[key] = event.value
            }
            return true
        }
        return false
    }

    /**
     * 获取实体所有可编辑的元数据模型
     */
    open fun getEditableEntityMeta(): List<Meta<*>> {
        return getAvailableEntityMeta().filter { it.index != -1 }.toList()
    }

    /**
     * 获取实体所有元数据模型
     */
    open fun getAvailableEntityMeta(): List<Meta<*>> {
        return metaCache.computeIfAbsent(javaClass) {
            AdyeshachAPI.registeredEntityMeta.filterKeys { it.isAssignableFrom(javaClass) }.values.flatten()
        }
    }

    open fun getTags(): Set<Map.Entry<String, String>> {
        return tag.entries
    }

    open fun getTag(key: String): String? {
        return tag[key]
    }

    open fun hasTag(key: String): Boolean {
        return tag.containsKey(key)
    }

    open fun setTag(key: String, value: String) {
        val event = AdyeshachTagUpdateEvent(this, key, value)
        if (event.call()) {
            if (event.value != null) {
                tag[key] = event.value!!
            } else {
                tag.remove(key)
            }
        }
    }

    open fun removeTag(key: String) {
        val event = AdyeshachTagUpdateEvent(this, key, null)
        if (event.call()) {
            if (event.value != null) {
                tag[key] = event.value!!
            } else {
                tag.remove(key)
            }
        }
    }

    internal fun getByteMaskKey(index: Int): String {
        return "\$${getAvailableEntityMeta().firstOrNull { it.index == index }!!.key.digest("md5").substring(0, 8)}"
    }

    companion object {

        val minecraftVersion = MinecraftVersion.majorLegacy
        val metaCache = ConcurrentHashMap<Class<*>, List<Meta<*>>>()
    }
}