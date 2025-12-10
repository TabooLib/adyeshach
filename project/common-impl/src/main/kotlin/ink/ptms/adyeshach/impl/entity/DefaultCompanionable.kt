package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.core.entity.Companionable
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.StandardTags
import ink.ptms.adyeshach.core.event.AdyeshachEntityCompanionEvent
import ink.ptms.adyeshach.core.util.errorBy

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.DefaultCompanionable
 *
 * 伴生关系的默认实现
 *
 * @author 坏黑
 * @since 2024/12/11
 */
interface DefaultCompanionable : Companionable {

    override fun getHost(): EntityInstance? {
        this as DefaultEntityInstance
        // 优先使用缓存
        val cache = cacheHostEntity
        if (cache != null) {
            return cache
        }
        // 从持久化标签恢复
        val hostId = getPersistentTag(StandardTags.COMPANION_HOST) ?: return null
        val host = manager?.getEntityByUniqueId(hostId)
        // 更新缓存
        if (host != null) {
            cacheHostEntity = host
        }
        return host
    }

    override fun getRootHost(): EntityInstance? {
        var current = getHost() ?: return null
        while (true) {
            val parent = (current as? Companionable)?.getHost() ?: return current
            current = parent
        }
    }

    override fun setHost(entity: EntityInstance?) {
        this as DefaultEntityInstance
        val previousHost = getHost()

        // 相同宿主，无需操作
        if (previousHost?.uniqueId == entity?.uniqueId) return

        // 事件
        if (!AdyeshachEntityCompanionEvent(this, entity, previousHost).call()) {
            return
        }

        // 从旧宿主移除
        previousHost?.let { oldHost ->
            oldHost as DefaultEntityInstance
            oldHost.companions.remove(uniqueId)
        }

        if (entity == null) {
            // 解除归属
            cacheHostEntity = null
            removePersistentTag(StandardTags.COMPANION_HOST)
        } else {
            // 校验 manager 一致
            if (entity.manager != manager) {
                errorBy("error-entity-manager-not-match")
            }
            // 避免循环归属
            var current: EntityInstance? = entity
            while (current != null) {
                if (current.uniqueId == uniqueId) {
                    errorBy("error-circular-companion")
                }
                current = (current as? Companionable)?.getHost()
            }
            // 设置归属
            entity as DefaultEntityInstance
            entity.companions.add(uniqueId)
            cacheHostEntity = entity
            setPersistentTag(StandardTags.COMPANION_HOST, entity.uniqueId)
            // 同步观察者列表
            syncViewersFromHost(entity)
        }
    }

    override fun hasHost(): Boolean {
        this as DefaultEntityInstance
        return cacheHostEntity != null || hasPersistentTag(StandardTags.COMPANION_HOST)
    }

    override fun isCompanion(): Boolean = hasHost()

    override fun getCompanions(): List<EntityInstance> {
        this as DefaultEntityInstance
        return companions.mapNotNull { manager?.getEntityByUniqueId(it) }
    }

    override fun getAllCompanions(): List<EntityInstance> {
        val result = mutableListOf<EntityInstance>()
        collectCompanions(this as EntityInstance, result)
        return result
    }

    override fun addCompanion(vararg entity: EntityInstance) {
        entity.forEach { it.setHost(this as EntityInstance) }
    }

    override fun removeCompanion(vararg entity: EntityInstance) {
        entity.forEach {
            if ((it as? Companionable)?.getHost()?.uniqueId == (this as EntityInstance).uniqueId) {
                it.setHost(null)
            }
        }
    }

    override fun clearCompanions() {
        removeCompanion(*getCompanions().toTypedArray())
    }

    /**
     * 从宿主同步观察者列表
     */
    private fun syncViewersFromHost(host: EntityInstance) {
        this as DefaultEntityInstance
        // 清空当前观察者
        viewPlayers.viewers.clear()
        viewPlayers.visible.clear()
        // 同步宿主的观察者列表
        viewPlayers.viewers.addAll(host.viewPlayers.viewers)
    }

    /**
     * 递归收集所有伴生实体
     */
    private fun collectCompanions(entity: EntityInstance, result: MutableList<EntityInstance>) {
        val companions = (entity as? Companionable)?.getCompanions() ?: return
        companions.forEach {
            result.add(it)
            collectCompanions(it, result)
        }
    }
}
