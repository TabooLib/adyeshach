package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityFireball
import ink.ptms.adyeshach.core.entity.EntityThrowable
import ink.ptms.adyeshach.core.entity.type.AdyItem
import ink.ptms.adyeshach.core.entity.type.AdyMinecart
import ink.ptms.adyeshach.core.event.AdyeshachEntityCreateEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.util.Vector
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit

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
            // 掉落物默认客户端运算重力
            // 需设置运动力为 0
            is AdyItem -> {
                entity.setNoGravity(true)
                entity.sendVelocity(Vector(0, 0, 0))
            }
        }
    }

    /**
     * TODO
     * 修正玩家类型的展示名称
     */
//    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
//    fun onGenerate(e: AdyeshachNaturalMetaGenerateEvent) {
//        val value = e.value
//        if (e.meta.key == "customName" && value is String) {
//            val length = if (e.entity.entityType == EntityTypes.PLAYER) 46 else 64
//            if (value.length > length) {
//                e.value = value.substring(0, length)
//                // 发送警告
//                console().sendLang("error-name-length", e.entity.id, e.entity.entityType, value.length, length)
//            }
//        }
//    }
}