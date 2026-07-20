package ink.ptms.adyeshach.impl.entity.handler

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.StandardTags
import ink.ptms.adyeshach.core.entity.manager.PlayerManager
import ink.ptms.adyeshach.impl.ServerTours
import ink.ptms.adyeshach.impl.entity.DefaultEntityInstance
import ink.ptms.adyeshach.impl.manager.VisibilityRefreshCoordinator.playersInGameTick
import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.handler.VisibilityHandler
 *
 * 负责实体的可见性检查和处理
 */
open class VisibilityHandler(protected val self: DefaultEntityInstance) {

    /**
     * 检查实体可见性
     * 处理玩家可见性检查的主入口
     */
    open fun checkVisible() {
        if (!prepareVisibilityCycle()) {
            return
        }
        // 同步可见状态
        syncVisibleState()
    }

    /**
     * 在主线程准备一次可见性检查
     * 所有实体先同步载具位置；伴生实体只做位置自愈，不独立参与玩家距离扫描。
     *
     * @return 是否需要继续计算该实体的玩家可见性
     */
    open fun prepareVisibilityCycle(): Boolean {
        // 同步到载具位置
        val clientPositionChanged = syncVehiclePosition()
        // 伴生实体跳过独立的可见性检查（由宿主驱动）
        if (self.isCompanion()) {
            // 伴生仍需执行载具位置校准，避免一次乘客 delta 漏同步后永久停在旧位置
            if (clientPositionChanged && self.viewPlayers.hasVisiblePlayer()) {
                self.positionHandler.broadcastTeleportAndHead(self.clientPosition.toLocation(), self.yaw)
            }
            return false
        }
        return true
    }

    /**
     * 仅为指定玩家重新检查宿主可见性
     * 玩家传送后的定点收敛不重复扫描其他在线玩家，伴生仍由宿主显隐传播。
     *
     * @param player 需要重新检查的玩家
     */
    open fun checkVisible(player: Player) {
        if (!self.isCompanion()) {
            handleVisible(player, self.isHide())
        }
    }

    /**
     * 在主线程复核并提交异步阶段生成的显隐候选
     * 提交前重新检查 ACL、当前显隐、hide、世界与距离；仅生成实体时检查区块可见性。
     *
     * @param player 候选观察者
     * @param expectedVisible 快照中的显隐状态
     * @param visible 候选目标显隐状态
     * @return 候选是否通过复核并完成提交
     */
    open fun commitVisibleCandidate(player: Player, expectedVisible: Boolean, visible: Boolean): Boolean {
        val viewPlayers = self.viewPlayers
        if (player.name !in viewPlayers.viewers || (player.name in viewPlayers.visible) != expectedVisible) {
            return false
        }
        val hidden = self.isHide()
        val inVisibleDistance = self.isInVisibleDistance(player)
        if (visible) {
            if (hidden || !inVisibleDistance || !isChunkVisible(player)) {
                return false
            }
        } else if (!hidden && (inVisibleDistance || ServerTours.isRoutePlaying(player))) {
            return false
        }
        return self.visible(player, visible)
    }

    /**
     * 同步到载具位置
     *
     * @return 客户端位置是否发生真实漂移
     */
    protected open fun syncVehiclePosition(): Boolean {
        if (self.isDisableVehicleCheckOnTick) {
            return false
        }
        val vehicle = self.cacheVehicleEntity
        if (vehicle != null) {
            // 乘客 attachment 允许合法高度偏移，仅跨世界或超出双方实体尺寸容差时判定为真实漂移。
            val vehiclePosition = vehicle.position
            val serverPosition = self.position
            val clientPosition = self.clientPosition
            val positionTolerance = self.entitySize.height + vehicle.entitySize.height + 2.0
            val positionToleranceSquared = positionTolerance * positionTolerance
            val serverPositionChanged = serverPosition.world != vehiclePosition.world || run {
                val deltaX = serverPosition.x - vehiclePosition.x
                val deltaY = serverPosition.y - vehiclePosition.y
                val deltaZ = serverPosition.z - vehiclePosition.z
                deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ > positionToleranceSquared
            }
            val clientPositionChanged = clientPosition.world != vehiclePosition.world || run {
                val deltaX = clientPosition.x - vehiclePosition.x
                val deltaY = clientPosition.y - vehiclePosition.y
                val deltaZ = clientPosition.z - vehiclePosition.z
                deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ > positionToleranceSquared
            }
            if (serverPositionChanged) {
                self.position = vehiclePosition.copy(yaw = serverPosition.yaw, pitch = serverPosition.pitch)
            }
            if (clientPositionChanged) {
                self.clientPosition = vehiclePosition.copy(yaw = clientPosition.yaw, pitch = clientPosition.pitch)
            }
            // 载具状态未变化时不进入标签更新链，避免重复触发持久标签事件。
            if (self.getPersistentTag(StandardTags.IS_IN_VEHICLE) != "true") {
                self.setPersistentTag(StandardTags.IS_IN_VEHICLE, "true")
            }
            return clientPositionChanged
        } else {
            if (self.hasPersistentTag(StandardTags.IS_IN_VEHICLE)) {
                self.removePersistentTag(StandardTags.IS_IN_VEHICLE)
            }
            return false
        }
    }

    /**
     * 同步可见状态
     */
    protected open fun syncVisibleState() {
        val entityManager = self.manager
        // hide 是实体级状态，每轮只解析一次，避免公共实体按玩家重复读取持久化标签。
        val hidden = self.isHide()
        if (entityManager is PlayerManager) {
            handleVisible(entityManager.owner, hidden)
        } else {
            playersInGameTick.forEach { handleVisible(it, hidden) }
        }
    }

    /**
     * 处理单个玩家的可见性
     *
     * 大量用户反馈的 NPC 概率性不可见问题，根本原因在于这个逻辑
     * 尝试性修复 - 2023/12/29: 玩家在可见范围内呆上一个检查周期后才会显示实体，并缩短检查周期 (5s -> 2s)
     * 尝试性修复 - 2024/02/27: 基于原版 PlayerChunkMap 的区块可见性决定实体可见性
     *
     * @param player 需要检查的玩家
     * @param hidden 本轮已解析的实体隐藏状态
     */
    protected open fun handleVisible(player: Player, hidden: Boolean) {
        val viewPlayers = self.viewPlayers
        // 是观察者
        if (player.name !in viewPlayers.viewers) {
            return
        }
        // 是可见的观察者
        if (player.name in viewPlayers.visible) {
            // 已显示实体只按距离与 hide 回收；区块可见结果可能瞬时波动，不能据此销毁展示实体
            if (hidden || (!self.isInVisibleDistance(player) && !ServerTours.isRoutePlaying(player))) {
                self.visible(player, false)
            }
        } else {
            // 是否在可视范围内 && 所在区块是否可见 && 实体未被隐藏 && 显示实体
            if (!hidden && self.isInVisibleDistance(player) && isChunkVisible(player)) {
                self.visible(player, true)
            }
        }
    }

    /**
     * 检查区块是否对玩家可见
     */
    protected open fun isChunkVisible(player: Player): Boolean {
        return Adyeshach.api().getMinecraftAPI().getHelper().isChunkVisible(player, self.chunkX, self.chunkZ)
    }
}
