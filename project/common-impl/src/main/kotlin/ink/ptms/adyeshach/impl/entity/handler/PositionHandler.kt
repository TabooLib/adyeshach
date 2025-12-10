package ink.ptms.adyeshach.impl.entity.handler

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.bukkit.data.EntityPosition
import ink.ptms.adyeshach.core.entity.StandardTags
import ink.ptms.adyeshach.core.entity.TickService
import ink.ptms.adyeshach.core.event.AdyeshachEntityHeadRotationEvent
import ink.ptms.adyeshach.core.util.fixYaw
import ink.ptms.adyeshach.core.util.modify
import ink.ptms.adyeshach.impl.DefaultAdyeshachAPI
import ink.ptms.adyeshach.impl.entity.DefaultEntityInstance
import org.bukkit.Location
import org.bukkit.util.Vector

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.handler.PositionHandler
 *
 * 负责实体的位置和视角管理
 */
class PositionHandler(private val self: DefaultEntityInstance) {

    /**
     * 传送实体到指定位置
     */
    fun teleport(entityPosition: EntityPosition) {
        teleport(entityPosition.toLocation())
    }

    /**
     * 传送实体到指定坐标
     */
    fun teleport(x: Double, y: Double, z: Double) {
        teleport(self.clientPosition.toLocation().modify(x, y, z))
    }

    /**
     * 传送实体到指定位置
     */
    fun teleport(location: Location) {
        // 异常角度警告
        if (location.yaw.isNaN() || location.pitch.isNaN()) {
            IllegalStateException("Invalid head rotation (yaw=${location.yaw}, pitch=${location.pitch})").printStackTrace()
        }
        
        // 处理事件
        val eventBus = DefaultAdyeshachAPI.localEventBus
        if (eventBus.callTeleport(self, location)) {
            eventBus.postTeleport(self, location)
        } else {
            return
        }
        
        val newPosition = EntityPosition.fromLocation(location)
        
        // 强制传送
        if (self.tag.containsKey(StandardTags.FORCE_TELEPORT)) {
            self.tag.remove(StandardTags.FORCE_TELEPORT)
        }
        // 如果坐标没变则不做处理
        else if (newPosition == self.position) {
            return
        }
        
        // 是否发生实质性位置变更
        val isMoved = self.position.x != newPosition.x || self.position.y != newPosition.y || self.position.z != newPosition.z
        
        // 是否切换世界
        if (self.position.world != newPosition.world) {
            self.position = newPosition
            self.despawn()
            self.respawn()
        }
        
        // 无管理器 || 孤立管理器 || 不允许进行位置同步
        if (self.manager == null || self.manager !is TickService || !self.allowSyncPosition()) {
            self.position = newPosition
            self.clientPosition = self.position
            Adyeshach.api().getMinecraftAPI().getEntityOperator().teleportEntity(
                self.getVisiblePlayers(),
                self.index,
                location.modify(yaw = self.entityType.fixYaw(location.yaw))
            )
        } else {
            self.clientPosition = newPosition
        }
        
        // 只有在位置发生变更时才进行 passengers 同步
        if (isMoved) {
            // 同步 passengers 位置
            self.getPassengers().forEach { it.teleport(location) }
            // 更新 passengers 信息
            self.refreshPassenger()
        }
    }

    /**
     * 设置实体动量
     */
    fun setVelocity(vector: Vector) {
        val eventBus = DefaultAdyeshachAPI.localEventBus
        if (eventBus.callVelocity(self, vector)) {
            self.deltaMovement = vector.clone()
        }
    }

    /**
     * 设置实体动量
     */
    fun setVelocity(x: Double, y: Double, z: Double) {
        setVelocity(Vector(x, y, z))
    }

    /**
     * 获取实体动量
     */
    fun getVelocity(): Vector {
        return self.deltaMovement.clone()
    }

    /**
     * 设置实体视角（看向某个位置）
     */
    fun setHeadRotation(location: Location, forceUpdate: Boolean = false) {
        val size = Adyeshach.api().getEntityTypeRegistry().getEntitySize(self.entityType)
        self.clientPosition.toLocation().add(0.0, size.height * 0.9, 0.0).also { entityLocation ->
            entityLocation.direction = location.clone().subtract(entityLocation).toVector()
            setHeadRotation(entityLocation.yaw, entityLocation.pitch, forceUpdate)
        }
    }

    /**
     * 设置实体视角（直接指定角度）
     */
    fun setHeadRotation(yaw: Float, pitch: Float, forceUpdate: Boolean = false) {
        if (AdyeshachEntityHeadRotationEvent(self, yaw, pitch, forceUpdate).call()) {
            // 强制更新
            if (forceUpdate) {
                self.position.yaw = yaw
                self.position.pitch = pitch
                self.clientPosition.yaw = yaw
                self.clientPosition.pitch = pitch
                Adyeshach.api().getMinecraftAPI().getEntityOperator().updateEntityLook(
                    player = self.getVisiblePlayers(),
                    entityId = self.index,
                    yaw = self.entityType.fixYaw(yaw),
                    pitch = pitch,
                    onGround = !self.entityPathType.isFly()
                )
            } else {
                teleport(self.clientPosition.toLocation().modify(yaw, pitch))
            }
        }
    }

    /**
     * 刷新实体位置（强制同步到客户端）
     */
    fun refreshPosition() {
        val location = self.getLocation()
        Adyeshach.api().getMinecraftAPI().getEntityOperator().teleportEntity(
            player = self.getVisiblePlayers(),
            entityId = self.index,
            location = location.modify(yaw = self.entityType.fixYaw(location.yaw))
        )
    }
}
