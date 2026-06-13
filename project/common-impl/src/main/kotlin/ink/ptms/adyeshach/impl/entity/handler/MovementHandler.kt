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
        // 发包看向控制器已经按观察者写入角度，本帧只同步位置，避免通用旋转包覆盖专属角度。
        val updateRotation = !rotationSyncSuppressed && (shouldUpdateRotation() || bodyYawChanged)
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
        self.clientPositionFixed = System.currentTimeMillis()
        val bodyLoc = self.positionHandler.runtimeBodyLocation().apply {
            x = self.clientPosition.x
            y = self.clientPosition.y
            z = self.clientPosition.z
            pitch = self.clientPosition.pitch
        }
        self.positionHandler.broadcastTeleportAndHead(bodyLoc, self.yaw)
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
        } else if (updateRotation) {
            syncEntityLookWithHead()
        }
        self.position = self.clientPosition
    }

    /**
     * Entity Look 用运行时身体 yaw，头部单独 Head Rotation
     */
    protected open fun syncEntityLookWithHead() {
        self.positionHandler.syncLookToClients(self.positionHandler.runtimeBodyYaw(), self.pitch, self.yaw)
    }
}
