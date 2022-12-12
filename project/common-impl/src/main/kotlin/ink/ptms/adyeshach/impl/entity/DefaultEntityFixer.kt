package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.api.event.AdyeshachEntityCreateEvent
import ink.ptms.adyeshach.api.event.AdyeshachEntityTeleportEvent
import ink.ptms.adyeshach.api.event.AdyeshachHeadRotationEvent
import ink.ptms.adyeshach.api.event.AdyeshachNaturalMetaGenerateEvent
import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.entity.EntityFireball
import ink.ptms.adyeshach.common.entity.EntityThrowable
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.type.AdyHuman
import ink.ptms.adyeshach.common.entity.type.AdyMinecart
import ink.ptms.adyeshach.common.entity.type.AdyWitherSkull
import ink.ptms.adyeshach.common.util.toGroundCenter
import org.bukkit.event.player.PlayerRespawnEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.console
import taboolib.common.platform.function.submit
import taboolib.module.lang.sendLang

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.DefaultEntityFixer
 *
 * @author 坏黑
 * @since 2022/8/18 10:49
 */
internal object DefaultEntityFixer {

    /**
     * 玩家复活后刷新附近所有实体
     */
    @SubscribeEvent
    fun onRespawn(e: PlayerRespawnEvent) {
        submit(delay = 20) {
            Adyeshach.api().getEntityFinder().getVisibleEntities(e.player) { it.isViewer(e.player) }.forEach { it.visible(e.player, true) }
        }
    }

    /**
     * 修正关联实体位置
     */
    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onTeleport(e: AdyeshachEntityTeleportEvent) {
        e.entity.getPassengers().forEach { it.teleport(e.location.clone().toGroundCenter()) }
    }

    /**
     * 修正特殊实体朝向
     */
    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onRotation(e: AdyeshachHeadRotationEvent) {
        when (e.entity) {
            is AdyWitherSkull -> {
                e.yaw += 180
            }
            is AdyMinecart -> {
                e.yaw -= 90
                e.pitch = 0f
            }
        }
    }

    /**
     * 修正特殊实体属性
     */
    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onCreate(e: AdyeshachEntityCreateEvent) {
        when (val entity = e.entity) {
            // 矿车不存在视角朝向
            is AdyMinecart -> {
                e.location.pitch = 0f
            }
            // 火球的视角朝向为移动方向
            // 默认均为 0
            is EntityFireball -> {
                e.location.yaw = 0f
                e.location.pitch = 0f
            }
            // 投掷物默认客户端运算重力
            is EntityThrowable -> {
                entity.setNoGravity(true)
            }
            // 玩家默认无皮肤
            is AdyHuman -> {
                entity.setSkinEnabled(true)
            }
        }
    }

    /**
     * 修正玩家类型的展示名称
     */
    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onGenerate(e: AdyeshachNaturalMetaGenerateEvent) {
        val value = e.value
        if (e.meta.key == "customName" && value is String) {
            val length = if (e.entity.entityType == EntityTypes.PLAYER) 46 else 64
            if (value.length > length) {
                e.value = value.substring(0, length)
                // 发送警告
                console().sendLang("error-name-length", e.entity.id, e.entity.entityType, value.length, length)
            }
        }
    }
}