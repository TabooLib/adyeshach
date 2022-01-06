package ink.ptms.adyeshach.common.entity

import ink.ptms.adyeshach.api.event.*
import ink.ptms.adyeshach.common.entity.type.AdyHuman
import ink.ptms.adyeshach.common.entity.type.AdyItem
import ink.ptms.adyeshach.common.entity.type.AdyMinecart
import ink.ptms.adyeshach.common.entity.type.AdyWitherSkull
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit

object Patch {

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: AdyeshachEntityTeleportEvent) {
        e.entity.getPassengers().forEach {
            it.teleport(e.location.clone().add(1.5, 0.0, 1.5))
        }
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: AdyeshachHeadRotationEvent) {
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

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: AdyeshachEntityCreateEvent) {
        when (e.entity) {
            is AdyMinecart -> {
                e.location.pitch = 0f
            }
            is EntityFireball -> {
                e.location.yaw = 0f
                e.location.pitch = 0f
            }
            is EntityThrowable -> {
                e.entity.setNoGravity(true)
            }
            is AdyHuman -> {
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

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: AdyeshachEntityVisibleEvent) {
        if (e.visible) {
            when (e.entity) {
                is AdyItem -> {
                    e.entity.setMetadata("item", e.entity.getItem())
                }
                is AdyHuman -> {
                    submit(delay = 1) {
                        e.entity.setHeadRotation(e.entity.yaw, e.entity.pitch)
                        e.entity.updateEquipment()
                    }
                    submit(delay = 5) {
                        if (e.entity.isDie) {
                            e.entity.die(e.viewer)
                        }
                        if (e.entity.isSleepingLegacy) {
                            e.entity.setSleeping(true)
                        }
                        if (e.entity.isHideFromTabList) {
                            e.entity.removePlayerInfo(e.viewer)
                        }
                    }
                }
                else -> {
                    // 确保让一些特殊的实体看向正确的位置
                    // 矿车，凋零头
                    submit(delay = 5) { e.entity.setHeadRotation(e.entity.yaw, e.entity.pitch) }
                }
            }
        }
    }
}