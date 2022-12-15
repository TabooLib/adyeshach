package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.StandardTags
import ink.ptms.adyeshach.core.util.encodePos
import ink.ptms.adyeshach.core.util.ifloor
import ink.ptms.adyeshach.impl.compat.CompatServerTours
import ink.ptms.adyeshach.impl.entity.manager.DefaultEventBus
import ink.ptms.adyeshach.impl.entity.manager.DefaultManager
import ink.ptms.adyeshach.impl.util.ChunkAccess
import org.bukkit.util.Vector
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue
import kotlin.math.floor
import kotlin.math.roundToInt

/** 获取事件总线 */
fun DefaultEntityInstance.getEventBus(): DefaultEventBus? {
    return (manager as? DefaultManager)?.defaultEventBus
}

/** 所在区块是否加载 */
fun DefaultEntityInstance.isChunkLoaded(): Boolean {
    return ChunkAccess.getChunkAccess(getWorld()).isChunkLoaded(floor(x).toInt() shr 4, floor(z).roundToInt() shr 4)
}

/**
 * 处理玩家可见
 * 确保客户端显示实体正常
 */
fun DefaultEntityInstance.handleTracker() {
    // 每 2 秒检查一次
    if (viewPlayers.visibleRefreshLocker.hasNext()) {
        // 复活在可视范围内的实体
        viewPlayers.getOutsidePlayers { isInVisibleDistance(it) }.forEach { player ->
            if (visible(player, true)) {
                viewPlayers.visible += player.name
            }
        }
        // 销毁不在可视范围内的实体
        viewPlayers.getViewPlayers { !isInVisibleDistance(it) }.forEach { player ->
            if (visible(player, false) && !CompatServerTours.isRoutePlaying(player)) {
                viewPlayers.visible -= player.name
            }
        }
    }
}

/** 处理移动力 */
fun DefaultEntityInstance.handleVelocity() {
    // 是否处于活动状态
    if (deltaMovement.lengthSquared() > 1E-6) {
        // 获取下一个移动位置
        val nextPosition = clientPosition.clone().add(deltaMovement.x, deltaMovement.y, deltaMovement.z)
        // 只有在向下移动的时候才会进行碰撞检测
        if (deltaMovement.y < 0) {
            val chunkAccess = ChunkAccess.getChunkAccess(getWorld())
            val blockHeight = chunkAccess.getBlockTypeAndHeight(nextPosition.x, nextPosition.y, nextPosition.z)
            if (blockHeight.first.isSolid) {
                clientPosition = nextPosition
                clientPosition.y = ifloor(nextPosition.y) + blockHeight.second + 0.01
                deltaMovement = Vector(0.0, 0.0, 0.0)
                return
            }
        }
        // 更新位置
        clientPosition = nextPosition
        // 更新速度
        deltaMovement = Vector(deltaMovement.x * 0.9, (deltaMovement.y - 0.08) * 0.98, deltaMovement.z * 0.9)
    }
}

/** 允许进行位置同步 */
fun DefaultEntityInstance.allowSyncPosition(): Boolean {
    // 不是傻子 && 存在可见玩家 && 所在区块已经加载
    return !isNitwit && viewPlayers.hasVisiblePlayer() && isChunkLoaded()
}

/** 同步位置 */
fun DefaultEntityInstance.syncPosition() {
    // 乘坐实体
    if (hasPersistentTag(StandardTags.IS_IN_VEHICLE)) {
        deltaMovement = Vector(0.0, 0.0, 0.0)
    } else {
        handleVelocity()
    }
    // 检查间隔
    if (clientUpdateInterval.hasNext()) {
        val updateRotation = (clientPosition.yaw - yaw).absoluteValue >= 1 || (clientPosition.pitch - pitch).absoluteValue >= 1
        val operator = Adyeshach.api().getMinecraftAPI().getEntityOperator()
        // 乘坐实体
        if (hasPersistentTag(StandardTags.IS_IN_VEHICLE)) {
            // 是否需要更新视角
            if (updateRotation) {
                operator.updateEntityLook(getVisiblePlayers(), index, yaw, pitch, false)
            }
            // 是否需要同步到载具位置
            if (vehicleSync + TimeUnit.SECONDS.toMillis(10) < System.currentTimeMillis()) {
                vehicleSync = System.currentTimeMillis()
                // 获取载具
                val vehicle = getVehicle()
                if (vehicle != null) {
                    position = vehicle.position
                }
            }
        } else {
            // 是否需要更新位置
            if (clientPosition != position) {
                // 计算差值
                val offset = clientPosition.clone().subtract(x, y, z)
                val x = encodePos(offset.x)
                val y = encodePos(offset.y)
                val z = encodePos(offset.z)
                val yaw = clientPosition.yaw
                val pitch = clientPosition.pitch
                val requireTeleport = x < -32768L || x > 32767L || y < -32768L || y > 32767L || z < -32768L || z > 32767L
                if (requireTeleport || clientPositionFixed + TimeUnit.SECONDS.toMillis(20) < System.currentTimeMillis()) {
                    clientPositionFixed = System.currentTimeMillis()
                    operator.teleportEntity(getVisiblePlayers(), index, clientPosition.toLocation(), false)
                } else {
                    val updatePosition = offset.lengthSquared() > 1E-6
                    if (updatePosition) {
                        if (updateRotation) {
                            operator.updateRelEntityMoveLook(getVisiblePlayers(), index, x.toShort(), y.toShort(), z.toShort(), yaw, pitch, false)
                        } else {
                            operator.updateRelEntityMove(getVisiblePlayers(), index, x.toShort(), y.toShort(), z.toShort(), false)
                        }
                    } else {
                        operator.updateEntityLook(getVisiblePlayers(), index, yaw, pitch, false)
                    }
                }
                position = clientPosition
            }
        }
    }
}