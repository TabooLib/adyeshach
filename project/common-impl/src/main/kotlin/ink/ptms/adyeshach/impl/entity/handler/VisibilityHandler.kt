package ink.ptms.adyeshach.impl.entity.handler

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.StandardTags
import ink.ptms.adyeshach.core.entity.manager.PlayerManager
import ink.ptms.adyeshach.impl.ServerTours
import ink.ptms.adyeshach.impl.entity.DefaultEntityInstance
import ink.ptms.adyeshach.impl.manager.DefaultManagerHandler.playersInGameTick
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
        // 伴生实体跳过独立的可见性检查（由宿主驱动）
        if (self.isCompanion()) return
        // 同步到载具位置
        syncVehiclePosition()
        // 同步可见状态
        syncVisibleState()
    }

    /**
     * 同步到载具位置
     */
    protected open fun syncVehiclePosition() {
        if (self.isDisableVehicleCheckOnTick) {
            return
        }
        val vehicle = self.cacheVehicleEntity
        if (vehicle != null) {
            self.position = vehicle.position.copy(yaw = self.position.yaw, pitch = self.position.pitch)
            self.clientPosition = vehicle.position.copy(yaw = self.clientPosition.yaw, pitch = self.clientPosition.pitch)
            self.setPersistentTag(StandardTags.IS_IN_VEHICLE, "true")
        } else {
            self.removePersistentTag(StandardTags.IS_IN_VEHICLE)
        }
    }

    /**
     * 同步可见状态
     */
    protected open fun syncVisibleState() {
        val entityManager = self.manager
        if (entityManager is PlayerManager) {
            handleVisible(entityManager.owner)
        } else {
            playersInGameTick.forEach { handleVisible(it) }
        }
    }

    /**
     * 处理单个玩家的可见性
     *
     * 大量用户反馈的 NPC 概率性不可见问题，根本原因在于这个逻辑
     * 尝试性修复 - 2023/12/29: 玩家在可见范围内呆上一个检查周期后才会显示实体，并缩短检查周期 (5s -> 2s)
     * 尝试性修复 - 2024/02/27: 基于原版 PlayerChunkMap 的区块可见性决定实体可见性
     */
    protected open fun handleVisible(player: Player) {
        val viewPlayers = self.viewPlayers
        // 是观察者
        if (player.name !in viewPlayers.viewers) {
            return
        }
        // 是可见的观察者
        if (player.name in viewPlayers.visible) {
            // 销毁不在可视范围内的实体
            if (!self.isInVisibleDistance(player) && !ServerTours.isRoutePlaying(player)) {
                self.visible(player, false)
            }
        } else {
            // 是否在可视范围内 && 所在区块是否可见 && 实体未被隐藏 && 显示实体
            if (self.isInVisibleDistance(player) && isChunkVisible(player) && !self.isHide()) {
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
