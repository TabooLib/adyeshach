package ink.ptms.adyeshach.impl.entity.handler

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachSettings
import ink.ptms.adyeshach.core.bukkit.data.EntityPosition
import ink.ptms.adyeshach.core.entity.StandardTags
import ink.ptms.adyeshach.core.entity.path.InterpolatedLocation
import ink.ptms.adyeshach.core.util.encodePos
import ink.ptms.adyeshach.core.util.fixYaw
import ink.ptms.adyeshach.core.util.ifloor
import ink.ptms.adyeshach.impl.entity.DefaultEntityInstance
import ink.ptms.adyeshach.impl.util.ChunkAccess
import org.bukkit.util.Vector
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.handler.MovementHandler
 *
 * 负责实体的移动处理和位置同步
 */
open class MovementHandler(protected val self: DefaultEntityInstance) {

    /** 上一次原地旋转兜底同步时间，用于替代每 tick 随机发包。 */
    var rotationFallbackSyncAt = 0L

    /**
     * 处理移动逻辑
     * 包括路径行走和物理模拟
     */
    open fun handleMove() {
        // 乘坐实体 || 冻结
        if (self.tag.containsKey(StandardTags.IS_IN_VEHICLE) || self.tag.containsKey(StandardTags.IS_FROZEN)) {
            self.deltaMovement = Vector(0.0, 0.0, 0.0)
            return
        }
        // 行走
        if (self.moveFrames != null) {
            handlePathWalking()
        }
        // 物理模拟
        handlePhysics()
    }

    /**
     * 处理路径行走
     */
    protected open fun handlePathWalking() {
        val moveFrames = self.moveFrames ?: return
        // 是否已抵达目的地
        if (moveFrames.isArrived()) {
            // 同步朝向
            self.moveTarget?.let { self.setHeadRotation(it.yaw, it.pitch, true) }
            self.moveTarget = null
            return
        }
        // 首次移动
        // 在单位首次移动之前，会有 0.25 秒的时间用于调整视角
        // 在这期间，单位会保持原地不动，并持有 "IS_MOVING_START" 标签
        if (!self.tag.containsKey(StandardTags.IS_MOVING) && !handleMovementStart(moveFrames)) {
            return
        }
        // 正在移动视角
        if (self.bionicSight?.isLooking == true) {
            return
        }
        self.tag.remove(StandardTags.IS_MOVING_START)
        // 获取下一个移动点
        val next = moveFrames.next() ?: return
        // 设置移动标签
        self.tag[StandardTags.IS_MOVING] = true
        // 默认会看向移动方向
        val eyeLocation = self.clientPosition.toLocation().add(0.0, self.entitySize.height * 0.9, 0.0)
        eyeLocation.direction = Vector(next.x, eyeLocation.y, next.z).subtract(eyeLocation.toVector())
        // 不会看向脚下
        if (eyeLocation.pitch < 90f) {
            next.yaw = EntityPosition.normalizeYaw(eyeLocation.yaw)
            next.pitch = EntityPosition.normalizePitch(eyeLocation.pitch)
        }
        // 更新位置
        if (next.yaw.isNaN() || next.pitch.isNaN()) {
            self.teleport(next.x, next.y, next.z)
        } else {
            self.teleport(next)
        }
        // 调试模式下显示路径
        if (AdyeshachSettings.debug) {
            self.world.spawnParticle(org.bukkit.Particle.VILLAGER_HAPPY, next.x, next.y, next.z, 2, 0.0, 0.0, 0.0, 0.0)
        }
    }

    /**
     * 处理移动开始阶段（视角调整）
     * @return 是否可以开始移动
     */
    protected open fun handleMovementStart(moveFrames: InterpolatedLocation): Boolean {
        var cur = 1
        var next = moveFrames.peek(cur)
        while (next != null && self.x == next.x && self.y == next.y && self.z == next.z) {
            cur++
            next = moveFrames.peek(cur)
        }
        if (next != null) {
            val startTime = self.tag[StandardTags.IS_MOVING_START]
            if (startTime == null || (startTime as Long) > System.currentTimeMillis()) {
                // 初始化等待时间
                if (startTime == null) {
                    self.tag[StandardTags.IS_MOVING_START] = System.currentTimeMillis() + 250
                }
                // 调整视角
                self.controllerLookAt(next.x, self.getEyeLocation().y, next.z, 35f, 40f)
                return false
            }
        }
        return true
    }

    /**
     * 处理物理模拟（重力、碰撞等）
     */
    protected open fun handlePhysics() {
        val deltaMovement = self.deltaMovement
        // 是否处于活动状态
        if (deltaMovement.lengthSquared() <= 1E-6) {
            return
        }
        // 获取下一个移动位置
        val nextPosition = self.clientPosition.clone().add(deltaMovement.x, deltaMovement.y, deltaMovement.z)
        // 只有在向下移动的时候才会进行碰撞检测
        if (deltaMovement.y < 0) {
            val chunkAccess = ChunkAccess.getChunkAccess(self.world)
            val blockHeight = chunkAccess.getBlockTypeAndHeight(nextPosition.x, nextPosition.y, nextPosition.z)
            if (blockHeight.first.isSolid) {
                self.clientPosition = nextPosition
                self.clientPosition.y = ifloor(nextPosition.y) + blockHeight.second + 0.01
                self.deltaMovement = Vector(0.0, 0.0, 0.0)
                return
            }
        }
        // 更新位置
        self.clientPosition = nextPosition
        // 更新速度（应用阻力和重力）
        self.deltaMovement = Vector(
            deltaMovement.x * 0.9,
            (deltaMovement.y - 0.08) * 0.98,
            deltaMovement.z * 0.9
        )
    }

    /**
     * 同步位置到客户端
     */
    open fun syncPosition() {
        val rotationSyncSuppressed = self.positionHandler.rotationSyncSuppressed
        val bodyYawChanged = self.positionHandler.advanceRuntimeBodyYawTowardHead()
        if (rotationSyncSuppressed) {
            self.positionHandler.rotationSyncSuppressed = false
        }
        // nitwit 的录像位移必须每帧携带 Look，否则客户端会按移动方向自行扭转玩家身体
        val forceNitwitRotation = self.isNitwit && self.clientPosition != self.position
        // 发包看向控制器已经按观察者写入角度，本帧只同步位置，避免通用旋转包覆盖专属角度。
        val updateRotation = !rotationSyncSuppressed && (forceNitwitRotation || shouldUpdateRotation() || bodyYawChanged)
        // 乘坐实体
        if (self.hasPersistentTag(StandardTags.IS_IN_VEHICLE)) {
            syncVehicleRotation(updateRotation)
        } else {
            syncFreeMovement(updateRotation)
        }
    }

    /**
     * 判断是否需要更新视角
     */
    protected open fun shouldUpdateRotation(): Boolean {
        if ((self.yaw - self.position.yaw).absoluteValue >= 1 || (self.pitch - self.position.pitch).absoluteValue >= 1) {
            return true
        }
        val now = System.currentTimeMillis()
        if (rotationFallbackSyncAt + TimeUnit.SECONDS.toMillis(5) > now) {
            return false
        }
        // 原地无角度变化时只低频补一次旋转包，避免高频兜底覆盖按观察者发包的控制器。
        rotationFallbackSyncAt = now
        return true
    }

    /**
     * 同步载具中实体的视角
     */
    protected open fun syncVehicleRotation(updateRotation: Boolean) {
        if (updateRotation && self.isDisableVehicleRotationSync) {
            syncEntityLookWithHead()
        }
    }

    /**
     * 同步自由移动实体的位置
     */
    protected open fun syncFreeMovement(updateRotation: Boolean) {
        if (self.clientPosition == self.position) {
            // 无位置差异时仍需同步旋转，保证运行时身体跟随不依赖位移包。
            if (updateRotation) {
                syncEntityLookWithHead()
            }
            return
        }
        // 计算差值
        val offset = self.clientPosition.clone().subtract(self.position)
        val x = encodePos(offset.x)
        val y = encodePos(offset.y)
        val z = encodePos(offset.z)
        // 是否需要传送
        val requireTeleport = x < -32768L || x > 32767L || y < -32768L || y > 32767L || z < -32768L || z > 32767L
        if (requireTeleport || self.clientPositionFixed + TimeUnit.SECONDS.toMillis(20) < System.currentTimeMillis()) {
            // 强制传送
            syncByTeleport()
        } else {
            // 相对移动
            syncByRelativeMove(offset, x, y, z, updateRotation)
        }
    }

    /**
     * 通过传送同步位置
     */
    protected open fun syncByTeleport() {
        val offset = self.clientPosition.clone().subtract(self.position)
        self.clientPositionFixed = System.currentTimeMillis()
        val bodyLoc = self.positionHandler.runtimeBodyLocation().apply {
            x = self.clientPosition.x
            y = self.clientPosition.y
            z = self.clientPosition.z
            pitch = self.clientPosition.pitch
        }
        self.positionHandler.broadcastTeleportAndHead(bodyLoc, self.yaw)
        syncPassengersAfterMove(offset.x, offset.y, offset.z)
        self.position = self.clientPosition
    }

    /**
     * 通过相对移动同步位置
     */
    protected open fun syncByRelativeMove(
        offset: EntityPosition,
        x: Long,
        y: Long,
        z: Long,
        updateRotation: Boolean
    ) {
        val updatePosition = offset.lengthSquared() > 1E-6
        if (updatePosition) {
            // 更新间隔检查
            if (!self.isIgnoredClientPositionUpdateInterval && !self.clientPositionUpdateInterval.hasNext()) {
                return
            }
            broadcastRelativeMove(x, y, z, updateRotation)
        } else if (updateRotation) {
            syncEntityLookWithHead()
        }
        syncPassengersAfterMove(offset.x, offset.y, offset.z)
        self.position = self.clientPosition
    }

    /**
     * 广播相对移动包，可选同步身体与头部朝向
     */
    protected open fun broadcastRelativeMove(x: Long, y: Long, z: Long, updateRotation: Boolean) {
        if (updateRotation) {
            val bodyYaw = self.entityType.fixYaw(self.positionHandler.runtimeBodyYaw())
            val headYaw = self.entityType.fixYaw(self.yaw)
            Adyeshach.api().getMinecraftAPI().getEntityOperator().updateRelEntityMoveLook(
                player = self.getVisiblePlayers(),
                entityId = self.index,
                x = x.toShort(),
                y = y.toShort(),
                z = z.toShort(),
                yaw = bodyYaw,
                pitch = self.pitch,
                onGround = !self.entityPathType.isFly()
            )
            Adyeshach.api().getMinecraftAPI().getEntityOperator().updateHeadRotation(
                player = self.getVisiblePlayers(),
                entityId = self.index,
                yaw = headYaw
            )
        } else {
            Adyeshach.api().getMinecraftAPI().getEntityOperator().updateRelEntityMove(
                player = self.getVisiblePlayers(),
                entityId = self.index,
                x = x.toShort(),
                y = y.toShort(),
                z = z.toShort(),
                onGround = !self.entityPathType.isFly()
            )
        }
    }

    /**
     * 载具位置同步后，给有 IS_IN_VEHICLE 标签的乘客按载具 delta 补发相对移动包。
     * 1.21.8 客户端不再从载具的 RelEntityMove 包自动同步乘客位置，需要手动补发。
     * 乘客不独立计算 delta，直接用载具的位移量发相同的 RelEntityMove 包并平移 position。
     * 1.21.8 客户端源码中的处理顺序：
     * - net.minecraft.client.network.ClientPlayNetworkHandler.onEntity(EntityS2CPacket) 解析 RelEntityMove，
     *   先通过 Entity.getTrackedPosition() 更新协议增量基准，再调用 Entity.updateTrackedPositionAndAngles()；
     * - Entity.updateTrackedPositionAndAngles() 会进入 PositionInterpolator.refreshPositionAndAngles()，
     *   LivingEntity（如名牌中间层 Silverfish）默认按 3 tick 插值；
     * - net.minecraft.entity.Entity.tickRiding() 同一 tick 还会调用 vehicle.updatePassengerPosition(this)，
     *   Entity.updatePassengerPosition() 根据 getPassengerRidingPos() 和 getVehicleAttachmentPos() 计算 attachment，
     *   最后通过 Entity.setPosition() 再写一次乘客位置；
     * - net.minecraft.entity.decoration.DisplayEntity.setPosition() 会调用 updateVisibilityBoundingBox()，
     *   DisplayEntity.shouldRender(distance) 也使用当前实体位置判断 viewRange，因此 TextDisplay 后代由骑乘链更新即可。
     *
     * 对 NPC -> Silverfish -> TextDisplay 这类嵌套骑乘，第一层 Silverfish 仍需要 b6936516 引入的补包；
     * TextDisplay 等后代则由客户端骑乘 tick 跟随。若递归发送 RelEntityMove，协议插值和 attachment 跟随会在同一 tick
     * 重复写入位置，表现为文字抽搐。这里仍递归平移后代的 clientPosition/position，供服务端后续可见性、传送和 delta
     * 计算使用，但通过 broadcastMove=false 禁止向第二层及更深层发送独立移动包。
     *
     * @param deltaX 载具本次同步的 X 位移
     * @param deltaY 载具本次同步的 Y 位移
     * @param deltaZ 载具本次同步的 Z 位移
     * @param broadcastMove 是否向本层乘客发送移动包，仅最外层调用为 true
     */
    protected open fun syncPassengersAfterMove(deltaX: Double, deltaY: Double, deltaZ: Double, broadcastMove: Boolean = true) {
        if (!self.passengerHandler.hasPassengers() || (deltaX == 0.0 && deltaY == 0.0 && deltaZ == 0.0)) {
            return
        }
        val x = encodePos(deltaX)
        val y = encodePos(deltaY)
        val z = encodePos(deltaZ)
        val requireTeleport = x < -32768L || x > 32767L || y < -32768L || y > 32767L || z < -32768L || z > 32767L
        self.getPassengers().forEach { passenger ->
            passenger as DefaultEntityInstance
            if (!passenger.hasPersistentTag(StandardTags.IS_IN_VEHICLE)) {
                return@forEach
            }
            // 平移乘客的 clientPosition，保持与载具的相对偏移
            passenger.clientPosition = passenger.clientPosition.clone().add(deltaX, deltaY, deltaZ)
            if (broadcastMove) {
                // delta 超出 short 范围（远距离传送），直接发 Teleport 包，不走 passenger.teleport() 以免被 IS_IN_VEHICLE 逻辑跳过
                if (requireTeleport) {
                    passenger.positionHandler.broadcastTeleportAndHead(passenger.clientPosition.toLocation(), passenger.yaw)
                } else {
                    passenger.movementHandler.broadcastRelativeMove(x, y, z, false)
                }
            }
            passenger.position = passenger.clientPosition
            passenger.movementHandler.syncPassengersAfterMove(deltaX, deltaY, deltaZ, false)
        }
    }

    /**
     * Entity Look 用运行时身体 yaw，头部单独 Head Rotation
     */
    protected open fun syncEntityLookWithHead() {
        self.positionHandler.syncLookToClients(self.positionHandler.runtimeBodyYaw(), self.pitch, self.yaw)
    }
}
