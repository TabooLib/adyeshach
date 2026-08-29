package ink.ptms.adyeshach.impl.manager

import ink.ptms.adyeshach.core.AdyeshachSettings
import ink.ptms.adyeshach.core.event.AdyeshachEntityDamageEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent

/**
 * 玩家与虚拟单位的交互冷却
 * 在 TraitCommand 等 MONITOR 监听之前取消事件，左键、右键共用，按玩家计算。
 */
internal object EntityInteractCooldown {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    private fun onInteract(e: AdyeshachEntityInteractEvent) {
        if (!e.isMainHand) {
            return
        }
        if (!tryAcquire(e.player.name)) {
            e.isCancelled = true
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    private fun onDamage(e: AdyeshachEntityDamageEvent) {
        if (!tryAcquire(e.player.name)) {
            e.isCancelled = true
        }
    }

    @SubscribeEvent
    private fun onQuit(e: PlayerQuitEvent) {
        AdyeshachSettings.interactCooldownBaffle?.reset(e.player.name)
    }

    /**
     * @return true 允许本次交互
     */
    private fun tryAcquire(id: String): Boolean {
        val baffle = AdyeshachSettings.interactCooldownBaffle ?: return true
        return baffle.hasNext(id)
    }
}
