package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.api.event.AdyeshachNaturalMetaGenerateEvent
import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.event.player.PlayerRespawnEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.warning

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
            Adyeshach.api().getEntityFinder().getVisibleEntities(e.player) { it.isViewer(e.player) }.forEach {
                it.visible(e.player, true)
            }
        }
    }

    /**
     * 修正玩家类型的展示名称
     */
    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: AdyeshachNaturalMetaGenerateEvent) {
        val value = e.value
        if (e.meta.key == "customName" && value is String) {
            val length = if (e.entity.entityType == EntityTypes.PLAYER) 46 else 64
            if (value.length > length) {
                e.value = value.substring(0, length)
                warning("NPC ${e.entity.id} created with name length greater than $length, truncating to ${value.substring(0, length)}")
            }
        }
    }
}