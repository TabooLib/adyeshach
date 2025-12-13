package ink.ptms.adyeshach.impl.entity.handler

import ink.ptms.adyeshach.core.bukkit.data.EntityPosition
import ink.ptms.adyeshach.core.event.AdyeshachEntityDestroyEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityRemoveEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntitySpawnEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityVisibleEvent
import ink.ptms.adyeshach.impl.DefaultAdyeshachAPI
import ink.ptms.adyeshach.impl.entity.DefaultEntityInstance
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.handler.LifecycleHandler
 *
 * 负责实体的生命周期管理（生成、销毁、重生）
 *
 * @author 坏黑
 * @since 2022/6/19
 */
open class LifecycleHandler(protected val self: DefaultEntityInstance) {

    /**
     * 准备生成实体（对单个玩家）
     * @return 是否成功准备生成
     */
    open fun prepareSpawn(viewer: Player, spawn: Runnable): Boolean {
        if (self.isDisableVisibleEvent || AdyeshachEntityVisibleEvent(self, viewer, true).call()) {
            // 使用事件系统控制实体显示
            if (DefaultAdyeshachAPI.localEventBus.callSpawn(self, viewer)) {
                spawn.run()
            }
            DefaultAdyeshachAPI.localEventBus.postSpawn(self, viewer)
            // 更新单位属性
            self.updateEntityMetadata(viewer)
            // 更新单位视角
            if (self.isRotationFixOnSpawn) {
                self.setHeadRotation(self.position.yaw, self.position.pitch, forceUpdate = true)
            }
            // 关联实体初始化
            if (self.isPassengerRefreshOnSpawn) {
                submit(delay = 2) { self.refreshPassenger(viewer) }
            }
            return true
        }
        return false
    }

    /**
     * 准备销毁实体（对单个玩家）
     * @return 是否成功准备销毁
     */
    open fun prepareDestroy(viewer: Player, destroy: Runnable): Boolean {
        if (self.isDisableVisibleEvent || AdyeshachEntityVisibleEvent(self, viewer, false).call()) {
            // 使用事件系统控制实体销毁
            if (DefaultAdyeshachAPI.localEventBus.callDestroy(self, viewer)) {
                destroy.run()
                DefaultAdyeshachAPI.localEventBus.postDestroy(self, viewer)
            }
            return true
        }
        return false
    }

    /**
     * 生成实体
     */
    open fun spawn(location: Location) {
        self.position = EntityPosition.fromLocation(location)
        self.clientPosition = self.position
        self.forViewers { self.visible(it, true) }
        AdyeshachEntitySpawnEvent(self).call()
    }

    /**
     * 重新生成实体
     * @throws IllegalStateException 如果实体已被移除
     */
    open fun respawn() {
        if (self.isRemoved) {
            error("Entity has been removed")
        }
        spawn(self.clientPosition.toLocation())
    }

    /**
     * 销毁实体
     * @param destroyPacket 是否发送销毁数据包
     * @param removeFromManager 是否从管理器中移除
     */
    open fun despawn(destroyPacket: Boolean = true, removeFromManager: Boolean = false) {
        if (destroyPacket) {
            self.forViewers { self.visible(it, false) }
            AdyeshachEntityDestroyEvent(self).call()
        }
        if (removeFromManager) {
            if (self.manager != null) {
                self.isRemoved = true
                // 销毁所有伴生实体（先处理，避免 manager 置空后无法获取）
                self.getCompanions().forEach { it.remove() }
                // 从宿主的伴生列表中移除自己
                self.cacheHostEntity?.let { host ->
                    host as DefaultEntityInstance
                    host.companions.remove(self)
                }
                self.manager!!.remove(self)
                AdyeshachEntityRemoveEvent(self).call()
                self.manager = null
            }
        }
    }
}
