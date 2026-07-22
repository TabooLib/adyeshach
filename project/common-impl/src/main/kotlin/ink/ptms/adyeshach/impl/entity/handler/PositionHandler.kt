package ink.ptms.adyeshach.impl.entity.handler

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.bukkit.data.EntityPosition
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.StandardTags
import ink.ptms.adyeshach.core.entity.TickService
import ink.ptms.adyeshach.core.event.AdyeshachEntityHeadRotationEvent
import ink.ptms.adyeshach.core.util.fixYaw
import ink.ptms.adyeshach.core.util.modify
import ink.ptms.adyeshach.impl.DefaultAdyeshachAPI
import ink.ptms.adyeshach.impl.entity.DefaultEntityInstance
import ink.ptms.adyeshach.impl.entity.controller.BionicSight
import org.bukkit.Location
import org.bukkit.util.Vector

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.handler.PositionHandler
 * 负责实体的位置和视角管理
 * 假实体位置与头身朝向的唯一实现入口
 * 状态约定：
 * - [DefaultEntityInstance.clientPosition]：运行时头部 yaw/pitch（协议与 [DefaultEntityInstance.yaw] 一致）
 * - [ink.ptms.adyeshach.impl.entity.DefaultEntityBase.position]：存档头部；getter 为 clone，写入头角须 [commitPersistedHead]
 * - [ink.ptms.adyeshach.core.entity.EntityBase.bodyYaw]：存档身体 yaw，null 表示生成时与头部一致
 * - [DefaultEntityInstance.clientBodyPosition].yaw：运行时身体 yaw；生成、传送和看向发包时作为身体朝向
 *
 * 协议：Entity Look 用身体 yaw，Entity Head Rotation 用头 yaw，顺序见 [syncLookToClients]
 *
 * @author sky
 */
open class PositionHandler(protected val self: DefaultEntityInstance) {

    /** 仅 setHead/controllerLookAt 触发身体自然跟随，手动 body_yaw 会关闭该状态。 */
    var bodyFollowHeadActive = false

    /** 发包看向控制器按观察者写入角度时，本帧不再广播统一旋转。 */
    var rotationSyncSuppressed = false

    /**
     * 传送实体到指定位置
     */
    open fun teleport(entityPosition: EntityPosition) {
        teleport(entityPosition.toLocation())
    }

    /**
     * 传送实体到指定坐标
     */
    open fun teleport(x: Double, y: Double, z: Double) {
        teleport(self.clientPosition.toLocation().modify(x, y, z))
    }

    /**
     * 传送实体到指定位置
     */
    open fun teleport(location: Location) {
        // 异常角度警告
        if (location.yaw.isNaN() || location.pitch.isNaN()) {
            IllegalStateException("Invalid head rotation (yaw=${location.yaw}, pitch=${location.pitch})").printStackTrace()
        }
        // 处理事件
        val eventBus = DefaultAdyeshachAPI.localEventBus
        if (!eventBus.callTeleport(self, location)) {
            return
        }
        eventBus.postTeleport(self, location)
        val newPosition = EntityPosition.fromLocation(location)
        val previousPosition = self.position
        val previousClientPosition = self.clientPosition
        // 强制传送
        if (self.tag.containsKey(StandardTags.FORCE_TELEPORT)) {
            self.tag.remove(StandardTags.FORCE_TELEPORT)
        } else if (newPosition == previousPosition) {
            // 如果坐标没变则不做处理
            return
        }
        // 是否切换世界
        val worldChanged = previousPosition.world != newPosition.world
        // 是否发生实质性位置变更
        val isMoved = worldChanged || previousPosition.x != newPosition.x || previousPosition.y != newPosition.y || previousPosition.z != newPosition.z
        // teleport 是整体位姿写入，目标 yaw 必须覆盖身体 yaw，避免头身分离。
        applyRuntimeBodyYaw(newPosition.yaw)
        val syncPositionByMovement = !worldChanged && self.manager is TickService && self.allowSyncPosition()
        if (worldChanged) {
            // 跨世界：先销毁旧世界状态，再同时写 position/clientPosition 为目标，随后在目标世界重新 spawn
            // 不能先用旧 clientPosition respawn，也不能发送跨世界 teleport 包
            self.despawn()
            self.position = newPosition
            self.clientPosition = newPosition
        } else if (!syncPositionByMovement) {
            // 无管理器 || 孤立管理器 || 不允许进行位置同步
            self.position = newPosition
            self.clientPosition = self.position
            val headYaw = EntityPosition.normalizeYaw(location.yaw)
            val bodyLoc = runtimeBodyLocation().apply {
                x = location.x
                y = location.y
                z = location.z
                pitch = location.pitch
            }
            broadcastTeleportAndHead(bodyLoc, headYaw)
        } else {
            self.clientPosition = newPosition
            if (self.isNitwit) {
                // nitwit 实体由外部线程（如 Horizon 播放）驱动位置写入，主线程 tick 与写入线程不同步。
                // 立即在当前线程同步，避免 offset 跨帧累积或 position 被设为未同步的 clientPosition 导致漂移瞬移。
                self.movementHandler.syncPosition()
            }
        }
        // 只有在位置发生变更时才进行 passengers 同步
        if (isMoved) {
            // 只有自由移动的 nitwit 会通过 syncPassengersAfterMove 同步子乘客；
            // 自身正在乘车时 syncPosition 只处理载具旋转，NameTag 等子乘客仍需在此补位置。
            val skipInVehicle = syncPositionByMovement && self.isNitwit && !self.hasPersistentTag(StandardTags.IS_IN_VEHICLE)
            val requireDirectPassengerTeleport = worldChanged || !syncPositionByMovement
            // 直接遍历并发引用集，避免连续移动每 tick 为乘客创建列表快照。
            val passengers = self.passengers.instances
            if (passengers.isNotEmpty()) {
                val vehiclePosition = if (requireDirectPassengerTeleport) previousClientPosition else self.clientPosition
                val destination = self.clientPosition.toLocation()
                passengers.forEach {
                    it as DefaultEntityInstance
                    if (it.hasPersistentTag(StandardTags.IS_IN_VEHICLE)) {
                        if (skipInVehicle) return@forEach
                        // 只有 Movement 不会继续补同步时，才修正原 diff 的远距离传送基准，不改变正常移动时序
                        // IS_IN_VEHICLE 乘客保留与载具的相对偏移，传送到载具新位置 + 原偏移
                        val passengerPosition = it.clientPosition
                        val dest = destination.clone().add(passengerPosition.x - vehiclePosition.x, passengerPosition.y - vehiclePosition.y, passengerPosition.z - vehiclePosition.z)
                        if (requireDirectPassengerTeleport) {
                            it.teleport(dest)
                        } else {
                            it.clientPosition = EntityPosition.fromLocation(dest)
                            it.positionHandler.broadcastTeleportAndHead(dest, it.yaw)
                            it.position = it.clientPosition
                        }
                    } else {
                        it.teleport(location)
                    }
                }
            }
            // 更新 passengers 信息
            self.refreshPassenger()
        }
        // 跨世界先递归更新完整乘客链的位置，最后再由宿主一次性生成伴生闭包
        if (worldChanged) {
            self.respawn()
        }
    }

    /**
     * 设置实体动量
     */
    open fun setVelocity(vector: Vector) {
        val eventBus = DefaultAdyeshachAPI.localEventBus
        if (eventBus.callVelocity(self, vector)) {
            self.deltaMovement = vector.clone()
        }
    }

    /**
     * 设置实体动量
     */
    open fun setVelocity(x: Double, y: Double, z: Double) {
        setVelocity(Vector(x, y, z))
    }

    /**
     * 获取实体动量
     */
    open fun getVelocity(): Vector {
        return self.deltaMovement.clone()
    }

    /**
     * 仅让头部看向目标点，身体 yaw 会按常规头部跟随逻辑追向头部
     */
    open fun setHeadRotation(location: Location, forceUpdate: Boolean = false) {
        val size = Adyeshach.api().getEntityTypeRegistry().getEntitySize(self.entityType)
        self.clientPosition.toLocation().add(0.0, size.height * 0.9, 0.0).also { entityLocation ->
            entityLocation.direction = location.clone().subtract(entityLocation).toVector()
            setHeadRotation(entityLocation.yaw, entityLocation.pitch, forceUpdate)
        }
    }

    /**
     * 设置头部 yaw/pitch
     */
    open fun setHeadRotation(yaw: Float, pitch: Float, forceUpdate: Boolean = false) {
        if (!AdyeshachEntityHeadRotationEvent(self, yaw, pitch, forceUpdate).call()) {
            return
        }
        commitPersistedHead(yaw, pitch)
        bodyFollowHeadActive = !forceUpdate
        val headYaw = self.yaw
        val headPitch = self.pitch
        if (forceUpdate) {
            // 强制头部同步用于逐帧回放等场景，不 respawn，但必须让身体 yaw 承载同一帧的 pitch 基准。
            applyRuntimeBodyYaw(headYaw)
        }
        val bodyYaw = runtimeBodyYaw()
        syncLookToClients(bodyYaw, headPitch, headYaw)
    }

    /**
     * 按原版头身表现推进身体 yaw，并同步当前 bodyYaw
     * 该入口用于 setHead 与 controllerLookAt 的头部驱动路径，避免头身长期分离和 refresh 后状态回退。
     *
     * @return 运行时身体 yaw 是否发生变化
     */
    open fun advanceRuntimeBodyYawTowardHead(): Boolean {
        if (!bodyFollowHeadActive || rotationSyncSuppressed) {
            return false
        }
        val currentBodyYaw = runtimeBodyYaw()
        val headYaw = self.yaw
        val nextBodyYaw = BionicSight.rotateTowards(currentBodyYaw, headYaw, 10f)
        val remainingDiff = BionicSight.wrapDegrees(headYaw - nextBodyYaw)
        val boundedBodyYaw = if (remainingDiff > 50f) {
            headYaw - 50f
        } else if (remainingDiff < -50f) {
            headYaw + 50f
        } else {
            nextBodyYaw
        }
        val normalized = EntityPosition.normalizeYaw(boundedBodyYaw)
        if (normalized == currentBodyYaw) {
            bodyFollowHeadActive = false
            return false
        }
        self.clientBodyPosition.yaw = normalized
        self.bodyYaw = normalized
        return true
    }

    /**
     * 设置头部和身体 yaw/pitch；显式看向目标点时使用该入口保证当帧头身对齐
     */
    open fun setHeadAndBodyRotation(location: Location) {
        val size = Adyeshach.api().getEntityTypeRegistry().getEntitySize(self.entityType)
        self.clientPosition.toLocation().add(0.0, size.height * 0.9, 0.0).also { entityLocation ->
            entityLocation.direction = location.clone().subtract(entityLocation).toVector()
            setHeadAndBodyRotation(entityLocation.yaw, entityLocation.pitch)
        }
    }

    /**
     * 设置头部和身体 yaw/pitch；显式写入角度时使用该入口保证当帧头身对齐
     */
    open fun setHeadAndBodyRotation(yaw: Float, pitch: Float) {
        if (!AdyeshachEntityHeadRotationEvent(self, yaw, pitch, false).call()) {
            return
        }
        commitPersistedHead(yaw, pitch)
        bodyFollowHeadActive = false
        val headYaw = self.yaw
        val headPitch = self.pitch
        // 显式头身对齐写入必须同帧落盘，避免下一次刷新/重生又回到旧身体角度。
        applyRuntimeBodyYaw(headYaw)
        if (self.entityType == EntityTypes.PLAYER) {
            // 玩家实体的生成包与 Look/Head 包在客户端存在头身时序差异，重生才能保证头身同帧对齐。
            self.despawn()
            self.respawn()
        } else {
            syncLookToClients(headYaw, headPitch, headYaw)
        }
    }

    /**
     * 编辑器/微调写入身体 yaw
     */
    open fun setBodyRotation(yaw: Float) {
        if (yaw.isNaN()) {
            IllegalStateException("Invalid body rotation (yaw=$yaw)").printStackTrace()
            return
        }
        val normalized = EntityPosition.normalizeYaw(yaw)
        bodyFollowHeadActive = false
        self.bodyYaw = normalized
        self.clientBodyPosition.yaw = normalized
        if (self.entityType == EntityTypes.PLAYER) {
            // 玩家单独 body_yaw 微调保留发包路径，让客户端按原版头身关系做平滑表现。
            broadcastTeleportAndHead(runtimeBodyLocation(), self.yaw)
        } else {
            syncLookToClients(normalized, self.pitch, self.yaw)
        }
    }

    /**
     * respawn 前把存档身体 yaw 灌入 clientBodyPosition，不写 bodyYaw
     */
    open fun prepareSpawnBodyFromArchive() {
        applyRuntimeBodyYaw(bodyYawForSpawn(), persist = false)
    }

    /**
     * body_yaw 重置为跟随当前存档头 yaw
     */
    open fun resetBodyYawToFollowHead() {
        bodyFollowHeadActive = false
        self.bodyYaw = null
        applyRuntimeBodyYaw(self.yaw, persist = false)
        if (self.entityType == EntityTypes.PLAYER) {
            // 重置身体 yaw 也是显式身体朝向写入，玩家需要沿用 refresh 的 Teleport 同步路径。
            broadcastTeleportAndHead(runtimeBodyLocation(), self.yaw)
        } else {
            syncLookToClients(runtimeBodyYaw(), self.pitch, self.yaw)
        }
    }

    /**
     * 刷新实体位置（强制同步到客户端）
     */
    open fun refreshPosition() {
        broadcastTeleportAndHead(runtimeBodyLocation(), self.yaw)
    }

    /**
     * 生成包用身体 Location（身体 yaw = bodyYaw 或头 yaw）
     */
    open fun spawnBodyLocation(): Location {
        return clientBodyPosition().modify(yaw = self.entityType.fixYaw(bodyYawForSpawn()))
    }

    /**
     * 协议刷新/传送用身体 Location（运行时身体 yaw）
     */
    open fun runtimeBodyLocation(): Location {
        return clientBodyPosition().modify(yaw = self.entityType.fixYaw(runtimeBodyYaw()))
    }

    /**
     * 获取运行时身体 yaw，供发包与外部读取统一使用
     */
    open fun runtimeBodyYaw(): Float {
        return effectiveBodyYaw(self.clientBodyPosition.yaw, self.yaw)
    }

    /**
     * 获取运行时头部 yaw，生成包需要与身体 yaw 分开写入
     */
    open fun headYaw(): Float {
        return self.yaw
    }

    /**
     * 存档身体 yaw；null 时与当前头 yaw 一致，身体跟随 tick 会写入当前身体状态
     */
    open fun bodyYawForSpawn(): Float {
        return EntityPosition.normalizeYaw(effectiveBodyYaw(self.bodyYaw ?: self.yaw, self.yaw))
    }

    /**
     * 获取最终使用的身体 yaw；nitwit 实体始终让身体与头部保持一致
     *
     * @param bodyYaw 原始身体 yaw
     * @param headYaw 当前头部 yaw
     * @return 最终使用的身体 yaw
     */
    open fun effectiveBodyYaw(bodyYaw: Float, headYaw: Float): Float {
        return if (self.isNitwit) EntityPosition.normalizeYaw(headYaw) else bodyYaw
    }

    /**
     * 写入头 yaw/pitch 到 clientPosition 并整体赋回 position（避免 position getter clone 丢写入）
     */
    open fun commitPersistedHead(yaw: Float, pitch: Float) {
        val normalizedYaw = EntityPosition.normalizeYaw(yaw)
        val normalizedPitch = EntityPosition.normalizePitch(pitch)
        self.clientPosition.yaw = normalizedYaw
        self.clientPosition.pitch = normalizedPitch
        self.position = self.clientPosition.clone()
    }

    /**
     * 写入运行时身体 yaw，并按需同步到可持久化 bodyYaw
     */
    open fun applyRuntimeBodyYaw(yaw: Float, persist: Boolean = true) {
        val normalized = EntityPosition.normalizeYaw(yaw)
        self.clientBodyPosition.yaw = normalized
        if (persist) {
            self.bodyYaw = normalized
        }
    }

    /**
     * 向可见玩家发送 Entity Look（身体）+ Head Rotation（头）
     */
    open fun syncLookToClients(bodyYaw: Float, pitch: Float, headYaw: Float) {
        val visible = self.getVisiblePlayers()
        if (visible.isEmpty()) {
            return
        }
        val operator = Adyeshach.api().getMinecraftAPI().getEntityOperator()
        operator.updateEntityLook(
            player = visible,
            entityId = self.index,
            yaw = self.entityType.fixYaw(effectiveBodyYaw(bodyYaw, headYaw)),
            pitch = pitch,
            onGround = !self.entityPathType.isFly(),
        )
        operator.updateHeadRotation(
            player = visible,
            entityId = self.index,
            yaw = self.entityType.fixYaw(headYaw),
        )
    }

    /**
     * 发送 Teleport（身体）+ Head Rotation（头），供刷新和无 tick 同步路径使用
     */
    open fun broadcastTeleportAndHead(bodyLocation: Location, headYaw: Float) {
        val visible = self.getVisiblePlayers()
        val operator = Adyeshach.api().getMinecraftAPI().getEntityOperator()
        val effectiveBodyLocation = if (self.isNitwit) {
            bodyLocation.clone().also { it.yaw = self.entityType.fixYaw(effectiveBodyYaw(it.yaw, headYaw)) }
        } else {
            bodyLocation
        }
        operator.teleportEntity(
            player = visible,
            entityId = self.index,
            location = effectiveBodyLocation,
            onGround = !self.entityPathType.isFly(),
        )
        operator.updateHeadRotation(
            player = visible,
            entityId = self.index,
            yaw = self.entityType.fixYaw(headYaw),
        )
    }

    /**
     * 获取运行时身体位置副本，避免调用方直接改写内部 EntityPosition
     */
    fun clientBodyPosition(): Location {
        return self.clientBodyPosition.toLocation()
    }
}
