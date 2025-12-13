package ink.ptms.adyeshach.impl.entity.handler

import ink.ptms.adyeshach.core.entity.Companionable
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.StandardTags
import ink.ptms.adyeshach.core.event.AdyeshachEntityCompanionEvent
import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.impl.entity.DefaultEntityInstance

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.handler.CompanionHandler
 *
 * 负责实体的伴生关系管理
 *
 * @author 坏黑
 * @since 2024/12/11
 */
open class CompanionHandler(protected val self: DefaultEntityInstance) {

    open fun getHost(): EntityInstance? {
        // 优先使用缓存
        val cache = self.cacheHostEntity
        if (cache != null) {
            return cache
        }
        // 从持久化标签恢复
        val hostId = self.getPersistentTag(StandardTags.COMPANION_HOST) ?: return null
        val host = self.manager?.getEntityByUniqueId(hostId)
        // 更新缓存
        if (host != null) {
            self.cacheHostEntity = host
        }
        return host
    }

    open fun getRootHost(): EntityInstance? {
        var current = getHost() ?: return null
        while (true) {
            val parent = (current as? Companionable)?.getHost() ?: return current
            current = parent
        }
    }

    open fun setHost(entity: EntityInstance?) {
        val previousHost = getHost()
        // 相同宿主，无需操作
        if (previousHost?.uniqueId == entity?.uniqueId) return
        // 事件
        if (!AdyeshachEntityCompanionEvent(self, entity, previousHost).call()) {
            return
        }
        // 从旧宿主移除
        previousHost?.let { oldHost ->
            oldHost as DefaultEntityInstance
            oldHost.companions.remove(self)
        }
        if (entity == null) {
            // 解除归属
            self.cacheHostEntity = null
            self.removePersistentTag(StandardTags.COMPANION_HOST)
        } else {
            // 避免循环归属
            var current: EntityInstance? = entity
            while (current != null) {
                if (current.uniqueId == self.uniqueId) {
                    errorBy("error-circular-companion")
                }
                current = (current as? Companionable)?.getHost()
            }
            // 设置归属
            entity as DefaultEntityInstance
            entity.companions.add(self)
            self.cacheHostEntity = entity
            self.setPersistentTag(StandardTags.COMPANION_HOST, entity.uniqueId)
            // 同步观察者列表
            syncViewersFromHost(entity)
        }
    }

    open fun hasHost(): Boolean {
        return self.cacheHostEntity != null || self.hasPersistentTag(StandardTags.COMPANION_HOST)
    }

    open fun isCompanion(): Boolean {
        return hasHost()
    }

    open fun getCompanions(): List<EntityInstance> {
        return self.companions.instances.toList()
    }

    open fun getAllCompanions(): List<EntityInstance> {
        val result = mutableListOf<EntityInstance>()
        collectCompanions(self, result)
        return result
    }

    open fun addCompanion(vararg entity: EntityInstance) {
        entity.forEach { it.setHost(self) }
    }

    open fun removeCompanion(vararg entity: EntityInstance) {
        entity.forEach {
            if ((it as? Companionable)?.getHost()?.uniqueId == self.uniqueId) {
                it.setHost(null)
            }
        }
    }

    open fun clearCompanions() {
        removeCompanion(*getCompanions().toTypedArray())
    }

    open fun verifyCompanion() {
        // 先解析待处理的 UUID
        self.manager?.let { self.companions.resolve(it) }
        // 验证并清理无效引用
        self.companions.verify()
        // 更新 cacheHostEntity
        self.cacheHostEntity = getHost()
    }

    /**
     * 从宿主同步观察者列表
     */
    protected open fun syncViewersFromHost(host: EntityInstance) {
        // 清空当前观察者
        self.viewPlayers.viewers.clear()
        self.viewPlayers.visible.clear()
        // 同步宿主的观察者列表
        self.viewPlayers.viewers.addAll(host.viewPlayers.viewers)
        self.viewPlayers.visible.addAll(host.viewPlayers.visible)
    }

    /**
     * 递归收集所有伴生实体
     */
    protected open fun collectCompanions(entity: EntityInstance, result: MutableList<EntityInstance>) {
        val companions = (entity as? Companionable)?.getCompanions() ?: return
        companions.forEach {
            result.add(it)
            collectCompanions(it, result)
        }
    }
}
