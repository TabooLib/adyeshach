package ink.ptms.adyeshach.impl.entity.handler

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.bukkit.data.EntityPosition
import ink.ptms.adyeshach.core.event.AdyeshachEntityDestroyEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityRemoveEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntitySpawnEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityVisibleEvent
import ink.ptms.adyeshach.impl.DefaultAdyeshachAPI
import ink.ptms.adyeshach.impl.entity.DefaultEntityInstance
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap

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

    // 记录整实体 despawn 后需要恢复的观察者，避免 respawn 再次走初始可见性筛选
    protected val pendingSpawnViewers: MutableSet<String> = ConcurrentHashMap.newKeySet<String>()

    /**
     * 准备生成实体（对单个玩家）
     *
     * @param viewer 观看者
     * @param impl 具体的生成实现
     * @return 是否成功
     */
    open fun prepareSpawn(viewer: Player, impl: Runnable): Boolean {
        if (self.isDisableVisibleEvent || (AdyeshachEntityVisibleEvent(self, viewer, true).call())) {
            if (DefaultAdyeshachAPI.localEventBus.callSpawn(self, viewer)) {
                impl.run()
                commitSpawnState(viewer)
                DefaultAdyeshachAPI.localEventBus.postSpawn(self, viewer)
                // 同步伴生实体可见性
                self.syncCompanionVisible(viewer, true)
                // 更新单位属性
                self.updateEntityMetadata(viewer)
                // 更新单位视角
                if (self.isRotationFixOnSpawn) {
                    self.setHeadRotation(self.position.yaw, self.position.pitch, forceUpdate = true)
                }
                // 关联实体初始化
                if (self.isPassengerRefreshOnSpawn) {
                    self.refreshPassenger(viewer)
                }
                return true
            }
        }
        return false
    }

    /**
     * 准备销毁实体（对单个玩家）
     *
     * @param viewer 观看者
     * @param impl 具体的销毁实现
     * @return 是否成功
     */
    open fun prepareDestroy(viewer: Player, impl: Runnable): Boolean {
        if (self.isDisableVisibleEvent || (AdyeshachEntityVisibleEvent(self, viewer, false).call())) {
            if (DefaultAdyeshachAPI.localEventBus.callDestroy(self, viewer)) {
                impl.run()
                commitDestroyState(viewer)
                DefaultAdyeshachAPI.localEventBus.postDestroy(self, viewer)
                // 同步伴生实体可见性
                self.syncCompanionVisible(viewer, false)
                return true
            }
        }
        return false
    }

    protected open fun commitSpawnState(viewer: Player) {
        self.viewPlayers.visible += viewer.name
        // 创建客户端对应表
        self.registerClientEntity(viewer)
        // 添加到可见实体索引
        self.updateVisibleEntityIndex(viewer, true)
    }

    protected open fun commitDestroyState(viewer: Player) {
        self.viewPlayers.visible -= viewer.name
        // 移除客户端对应表
        self.unregisterClientEntity(viewer)
        // 从可见实体索引中移除
        self.updateVisibleEntityIndex(viewer, false)
    }

    /**
     * 生成实体
     */
    open fun spawn(location: Location) {
        self.position = EntityPosition.fromLocation(location)
        self.clientPosition = self.position
        val viewers = getSpawnViewers()
        // 伴生实体需要使用内部方法（visible 接口会拒绝伴生实体的操作）
        if (self.isCompanion()) {
            viewers.forEach { self.handleCompanionVisible(it, true) }
        } else {
            viewers.forEach { self.visible(it, true) }
        }
        AdyeshachEntitySpawnEvent(self).call()
    }

    protected open fun getSpawnViewers(): List<Player> {
        val restoredViewers = pendingSpawnViewers.mapNotNull { Bukkit.getPlayerExact(it) }.filter {
            it.hasMetadata("adyeshach_setup") &&
                it.name in self.viewPlayers.viewers &&
                it.world.name == self.world.name
        }
        pendingSpawnViewers.clear()
        if (restoredViewers.isNotEmpty()) {
            return restoredViewers
        }
        val helper = Adyeshach.api().getMinecraftAPI().getHelper()
        return Bukkit.getOnlinePlayers().filter {
            it.name in self.viewPlayers.viewers &&
                it.hasMetadata("adyeshach_setup") &&
                it.world.name == self.world.name &&
                self.isInVisibleDistance(it) &&
                helper.isChunkVisible(it, self.chunkX, self.chunkZ)
        }
    }

    protected open fun rememberSpawnViewers() {
        pendingSpawnViewers.clear()
        pendingSpawnViewers.addAll(self.viewPlayers.visible)
    }

    /**
     * 重新生成实体
     * @throws IllegalStateException 如果实体已被移除
     */
    open fun respawn() {
        if (self.isRemoved) {
            error("Entity has been removed")
        }
        if (pendingSpawnViewers.isEmpty()) {
            rememberSpawnViewers()
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
            rememberSpawnViewers()
            // 伴生实体需要使用内部方法（visible 接口会拒绝伴生实体的操作）
            if (self.isCompanion()) {
                self.forViewers { self.handleCompanionVisible(it, false) }
            } else {
                self.forViewers { self.visible(it, false) }
            }
            AdyeshachEntityDestroyEvent(self).call()
        }
        if (removeFromManager) {
            if (self.manager != null) {
                pendingSpawnViewers.clear()
                self.isRemoved = true
                // 销毁所有伴生实体（先处理，避免 manager 置空后无法获取）
                self.getCompanions().forEach { it.remove() }
                // 从宿主的伴生列表中移除自己
                self.cacheHostEntity?.let { host ->
                    host as DefaultEntityInstance
                    host.companions.remove(self)
                }
                // 从载具中脱离
                self.cacheVehicleEntity?.removePassenger(self)
                // 清空所有乘客
                self.clearPassengers()
                // 从管理器中移除
                self.manager!!.remove(self)
                AdyeshachEntityRemoveEvent(self).call()
                self.manager = null
            }
        }
    }
}
