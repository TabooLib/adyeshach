package ink.ptms.adyeshach.impl.entity.handler

import ink.ptms.adyeshach.core.entity.Companionable
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.StandardTags
import ink.ptms.adyeshach.core.event.AdyeshachEntityCompanionEvent
import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.impl.entity.DefaultEntityInstance
import org.bukkit.Bukkit
import org.bukkit.entity.Player

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
        self.cacheHostEntity = host
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
        // 恢复宿主后按宿主状态协调 viewers / visible
        val host = self.cacheHostEntity
        if (host != null) {
            syncViewersFromHost(host)
        }
    }

    /**
     * 按宿主对观察者的可见结果收敛伴生真实 visible
     * actual(companion, player) 必须是 actual(host, player) 与伴生 ACL 的子集；
     * 私有伴生挂公共宿主时仅自身 viewers 可显示，禁止宿主公共 viewers 泄漏。
     *
     * @param viewer 观察者
     * @param visible 宿主侧目标可见状态
     */
    open fun syncVisibleFromHost(viewer: Player, visible: Boolean) {
        val host = getHost()
        // 伴生只能出现在宿主已经提交可见的玩家侧
        if (visible && (host == null || viewer.name !in host.viewPlayers.visible)) {
            return
        }
        // 私有伴生挂公共宿主：仅允许伴生自身已有 viewers 看到
        val privateOnPublic = !self.isPublic() && host?.isPublic() == true
        if (privateOnPublic) {
            if (viewer.name !in self.viewPlayers.viewers) {
                // ACL 拒绝时若仍有残留 visible，只走真实 destroy 收敛
                if (viewer.name in self.viewPlayers.visible) {
                    self.handleCompanionVisible(viewer, false)
                }
                return
            }
        } else if (host != null) {
            // 其他组合继承宿主 viewers（距离隐藏只动 visible，不误删 viewers）
            if (viewer.name in host.viewPlayers.viewers) {
                self.viewPlayers.viewers.add(viewer.name)
            } else {
                self.viewPlayers.viewers.remove(viewer.name)
            }
        }
        val currentlyVisible = viewer.name in self.viewPlayers.visible
        if (visible == currentlyVisible) {
            if (!visible) {
                // 当前层已经隐藏时仍向下清理 viewers / visible，避免退出清理在残缺伴生树中提前截断。
                self.syncCompanionVisible(viewer, false)
            }
            return
        }
        self.handleCompanionVisible(viewer, visible)
    }

    /**
     * 从宿主同步观察者列表
     * 仅协调 viewers，visible 经 [syncVisibleFromHost] 与宿主差异收敛，禁止直接 clear/addAll visible。
     */
    protected open fun syncViewersFromHost(host: EntityInstance) {
        // 私有 NPC 伴生到公共 NPC 时，不同步观察者（保持私有 NPC 的访问控制）
        if (!self.isPublic() && host.isPublic()) {
            // manager.create 的 callback 尚未结束时只建立关系，必须等待配置与 CreateEvent 完成后再 spawn
            if (self.isCreated) {
                convergeVisibleFromHost(host)
            }
            return
        }
        // 同步宿主的观察者列表（仅 viewers），一致时不触发 clear 的快照与回调遍历。
        if (self.viewPlayers.viewers != host.viewPlayers.viewers) {
            self.viewPlayers.viewers.clear()
            self.viewPlayers.viewers.addAll(host.viewPlayers.viewers)
        }
        // manager.create 的 callback 尚未结束时只建立关系，必须等待配置与 CreateEvent 完成后再 spawn
        if (self.isCreated) {
            convergeVisibleFromHost(host)
        }
    }

    /**
     * 对 host.visible 与 self.visible 做差异收敛
     * 在线玩家走真实 spawn/destroy；离线状态由玩家退出清理入口统一释放。
     */
    protected open fun convergeVisibleFromHost(host: EntityInstance) {
        val hostVisible = host.viewPlayers.visible
        val selfVisible = self.viewPlayers.visible
        // 宿主已可见而伴生未可见 → 尝试 spawn
        hostVisible.forEach { name ->
            if (name in selfVisible) {
                return@forEach
            }
            val player = Bukkit.getPlayerExact(name) ?: return@forEach
            syncVisibleFromHost(player, true)
        }
        // 伴生已可见而宿主未可见 → destroy
        selfVisible.forEach { name ->
            if (name in hostVisible) {
                return@forEach
            }
            val player = Bukkit.getPlayerExact(name) ?: return@forEach
            syncVisibleFromHost(player, false)
        }
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
