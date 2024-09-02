package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachSettings
import ink.ptms.adyeshach.core.bukkit.data.EntityPosition
import ink.ptms.adyeshach.core.entity.StandardTags
import ink.ptms.adyeshach.core.entity.TickService
import ink.ptms.adyeshach.core.entity.manager.PlayerManager
import ink.ptms.adyeshach.core.entity.path.PathFinderHandler
import ink.ptms.adyeshach.core.entity.path.ResultNavigation
import ink.ptms.adyeshach.core.util.encodePos
import ink.ptms.adyeshach.core.util.ifloor
import ink.ptms.adyeshach.core.util.plus
import ink.ptms.adyeshach.impl.ServerTours
import ink.ptms.adyeshach.impl.manager.DefaultManagerHandler.playersInGameTick
import ink.ptms.adyeshach.impl.util.ChunkAccess
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import taboolib.common.util.random
import taboolib.common5.clong
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

/** 所在区块是否加载 */
fun DefaultEntityInstance.isChunkLoaded(): Boolean {
    return ChunkAccess.getChunkAccess(world).isChunkLoaded(chunkX, chunkZ)
}

/** 更新管理器标签 */
fun DefaultEntityInstance.updateManagerTags() {
    // 孤立单位
    if (manager == null || manager !is TickService) {
        tag[StandardTags.ISOLATED] = true
    } else {
        tag.remove(StandardTags.ISOLATED)
        // 公共单位
        if (manager!!.isPublic()) {
            tag[StandardTags.IS_PUBLIC] = true
            tag.remove(StandardTags.IS_PRIVATE)
        } else {
            tag[StandardTags.IS_PRIVATE] = true
            tag.remove(StandardTags.IS_PUBLIC)
        }
        // 临时单位
        if (manager!!.isTemporary()) {
            tag[StandardTags.IS_TEMPORARY] = true
        } else {
            tag.remove(StandardTags.IS_TEMPORARY)
        }
    }
}

/** 更新移动路径 **/
fun DefaultEntityInstance.updateMoveFrames() {
    // 傻子或没有目的地
    if (isNitwit || moveTarget == null) {
        // 移除移动路径
        if (moveFrames != null) {
            moveFrames = null
            // 移除移动标签
            tag.remove(StandardTags.IS_MOVING)
        }
        return
    }
    // 设置标签
    setTag(StandardTags.IS_PATHFINDING, true)
    // 请求路径
    PathFinderHandler.request(position.toLocation(), moveTarget!!, entityPathType) {
        it as ResultNavigation
        // 移除标签
        removeTag(StandardTags.IS_PATHFINDING)
        // 按照路径移动
        controllerMoveBy(it.pointList.map { v -> v.toLocation(world) })
    }
}

/**
 * 处理玩家可见
 * 确保客户端显示实体正常
 *
 * 大量用户反馈的 NPC 概率性不可见问题，根本原因在于这个逻辑写垃圾
 * 尝试性修复 - 2023/12/29: 玩家在可见范围内呆上一个检查周期后才会显示实体，并缩短检查周期 (5s -> 2s)
 * 尝试性修复 - 2024/02/27: 基于原版 PlayerChunkMap 的区块可见性决定实体可见性
 */
fun DefaultEntityInstance.handleTracker() {
    // 检查间隔
    if (viewPlayers.visibleRefreshLocker.hasNext()) {
        // 同步到载具位置
        val vehicle = getVehicle()
        if (vehicle != null) {
            position = vehicle.position.clone()
            clientPosition = vehicle.position.clone()
        }
        // 同步可见状态
        val entityManager = manager
        if (entityManager is PlayerManager) {
            handleTracker(entityManager.owner)
        } else {
            playersInGameTick.forEach { handleTracker(it) }
        }
    }
}

private fun DefaultEntityInstance.handleTracker(player: Player) {
    // 是观察者
    if (player.name in viewPlayers.viewers) {
        // 是可见的观察者
        if (player.name in viewPlayers.visible) {
            // 销毁不在可视范围内的实体
            if (!isInVisibleDistance(player) && !ServerTours.isRoutePlaying(player) && visible(player, false)) {
                viewPlayers.visible -= player.name
            }
        } else {
            // 属否在可视范围内 && 所在区块是否可见 && 显示实体
            if (isInVisibleDistance(player) && Adyeshach.api().getMinecraftAPI().getHelper().isChunkVisible(player, chunkX, chunkZ) && visible(player, true)) {
                viewPlayers.visible += player.name
            }
        }
    }
}

/** 处理移动 */
fun DefaultEntityInstance.handleMove() {
    // 乘坐实体 || 冻结
    if (hasTag(StandardTags.IS_IN_VEHICLE) || hasTag(StandardTags.IS_FROZEN)) {
        deltaMovement = Vector(0.0, 0.0, 0.0)
        return
    }
    // 行走
    if (moveFrames != null) {
        // 是否已抵达目的地
        if (moveFrames!!.isArrived()) {
            // 同步朝向
            moveTarget?.let { setHeadRotation(it.yaw, it.pitch, true) }
            moveTarget = null
            return
        }
        // 首次移动
        // 在单位首次移动之前，会有 0.25 秒的时间用于调整视角
        // 在这期间，单位会保持原地不动，并持有 "IS_MOVING_START" 标签
        if (!tag.containsKey(StandardTags.IS_MOVING)) {
            var cur = 1
            var next = moveFrames!!.peek(cur)
            while (next != null && x == next.x && y == next.y && z == next.z) {
                cur++
                next = moveFrames!!.peek(cur)
            }
            if (next != null && (tag[StandardTags.IS_MOVING_START] == null || tag[StandardTags.IS_MOVING_START].clong > System.currentTimeMillis())) {
                // 初始化等待时间
                if (tag[StandardTags.IS_MOVING_START] == null) {
                    tag[StandardTags.IS_MOVING_START] = System.currentTimeMillis() + 250
                }
                // 调整视角
                controllerLookAt(next.x, getEyeLocation().y, next.z, 35f, 40f)
                return
            }
        }
        // 正在移动视角
        if (bionicSight.isLooking) {
            return
        }
        tag.remove(StandardTags.IS_MOVING_START)
        // 获取下一个移动点
        val next = moveFrames!!.next()
        if (next != null) {
            // 设置移动标签
            tag[StandardTags.IS_MOVING] = true
            // 默认会看向移动方向
            val eyeLocation = clientPosition.toLocation().plus(y = entitySize.height * 0.9)
            // 设置方向
            eyeLocation.direction = Vector(next.x, eyeLocation.y, next.z).subtract(eyeLocation.toVector())
            // 不会看向脚下
            if (eyeLocation.pitch < 90f) {
                next.yaw = EntityPosition.normalizeYaw(eyeLocation.yaw)
                next.pitch = EntityPosition.normalizePitch(eyeLocation.pitch)
            }
            // 更新位置
            if (next.yaw.isNaN() || next.pitch.isNaN()) {
                teleport(next.x, next.y, next.z)
            } else {
                teleport(next)
            }
            // 调试模式下显示路径
            if (AdyeshachSettings.debug) {
                world.spawnParticle(org.bukkit.Particle.VILLAGER_HAPPY, next.x, next.y, next.z, 2, 0.0, 0.0, 0.0, 0.0)
            }
        }
    }
    // 移动力
    handleVelocity()
}

/** 处理移动力 */
fun DefaultEntityInstance.handleVelocity() {
    // 是否处于活动状态
    if (deltaMovement.lengthSquared() > 1E-6) {
        // 获取下一个移动位置
        val nextPosition = clientPosition.clone().add(deltaMovement.x, deltaMovement.y, deltaMovement.z)
        // 只有在向下移动的时候才会进行碰撞检测
        if (deltaMovement.y < 0) {
            val chunkAccess = ChunkAccess.getChunkAccess(world)
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
    val updateRotation = (yaw - position.yaw).absoluteValue >= 1 || (pitch - position.pitch).absoluteValue >= 1 || random(0.2)
    val operator = Adyeshach.api().getMinecraftAPI().getEntityOperator()
    // 乘坐实体
    if (hasPersistentTag(StandardTags.IS_IN_VEHICLE)) {
        // 是否需要更新视角
        if (updateRotation) {
            operator.updateEntityLook(getVisiblePlayers(), index, yaw, pitch, true)
        }
    } else {
        // 是否需要更新位置
        if (clientPosition != position) {
            // 计算差值
            val offset = clientPosition.clone().subtract(position)
            val x = encodePos(offset.x)
            val y = encodePos(offset.y)
            val z = encodePos(offset.z)
            val requireTeleport = x < -32768L || x > 32767L || y < -32768L || y > 32767L || z < -32768L || z > 32767L
            if (requireTeleport || clientPositionFixed + TimeUnit.SECONDS.toMillis(20) < System.currentTimeMillis()) {
                clientPositionFixed = System.currentTimeMillis()
                operator.teleportEntity(getVisiblePlayers(), index, clientPosition.toLocation(), !entityPathType.isFly())
                position = clientPosition
            } else {
                val updatePosition = offset.lengthSquared() > 1E-6
                if (updatePosition) {
                    // 更新间隔
                    if (isIgnoredClientPositionUpdateInterval || clientPositionUpdateInterval.hasNext()) {
                        if (updateRotation) {
                            operator.updateRelEntityMoveLook(getVisiblePlayers(), index, x.toShort(), y.toShort(), z.toShort(), yaw, pitch, !entityPathType.isFly())
                        } else {
                            operator.updateRelEntityMove(getVisiblePlayers(), index, x.toShort(), y.toShort(), z.toShort(), !entityPathType.isFly())
                        }
                        position = clientPosition
                    }
                } else {
                    operator.updateEntityLook(getVisiblePlayers(), index, yaw, pitch, !entityPathType.isFly())
                    position = clientPosition
                }
            }
        }
    }
}