package ink.ptms.adyeshach.internal.listener

import ink.ptms.adyeshach.api.event.AdyeshachEntityCreateEvent
import ink.ptms.adyeshach.common.entity.type.AdyHuman
import taboolib.common.platform.event.SubscribeEvent

object ListenerPlayerSkin {

    @SubscribeEvent
    fun e(e: AdyeshachEntityCreateEvent) {
        if (e.entity is AdyHuman) {
            e.entity.setSkinCapeEnabled(true)
            e.entity.setSkinHatEnabled(true)
            e.entity.setSkinJacketEnabled(true)
            e.entity.setSkinLeftPantsEnabled(true)
            e.entity.setSkinLeftSleeveEnabled(true)
            e.entity.setSkinRightPantsEnabled(true)
            e.entity.setSkinRightSleeveEnabled(true)
        }
    }
}