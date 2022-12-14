package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.entity.StandardTags
import ink.ptms.adyeshach.common.util.encodePos
import ink.ptms.adyeshach.common.util.plugin.CompatServerTours
import kotlin.math.absoluteValue
import kotlin.math.floor
import kotlin.math.roundToInt

/** 所在区块是否加载 */
fun DefaultEntityInstance.isChunkLoaded(): Boolean {
    return DefaultEntityInstance.getChunkAccess(getWorld()).isChunkLoaded(floor(x).toInt() shr 4, floor(z).roundToInt() shr 4)
}

/** 处理玩家可见 */
fun DefaultEntityInstance.handleTracker() {
    // 确保客户端显示实体正常
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

/** 同步位置 */
fun DefaultEntityInstance.syncPosition() {
    // 不需要更新
    if (clientPosition == position) {
        return
    }
    // 更新间隔
    if (clientUpdateInterval.hasNext()) {
        val updateRotation = (clientPosition.yaw - yaw).absoluteValue >= 1 || (clientPosition.pitch - pitch).absoluteValue >= 1
        val operator = Adyeshach.api().getMinecraftAPI().getEntityOperator()
        // 乘坐实体
        if (hasPersistentTag(StandardTags.IS_IN_VEHICLE)) {
            // 是否需要更新视角
            if (updateRotation) {
                operator.updateEntityLook(getVisiblePlayers(), index, yaw, pitch, false)
            }
            return
        }
        val offset = clientPosition.clone().subtract(x, y, z)
        val x = encodePos(offset.x)
        val y = encodePos(offset.y)
        val z = encodePos(offset.z)
        val yaw = clientPosition.yaw
        val pitch = clientPosition.pitch
        val requireTeleport = x < -32768L || x > 32767L || y < -32768L || y > 32767L || z < -32768L || z > 32767L
        if (requireTeleport || clientPositionFixed++ > 400) {
            clientPositionFixed = 0
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