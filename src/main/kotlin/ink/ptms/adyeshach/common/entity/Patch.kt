package ink.ptms.adyeshach.common.entity

import ink.ptms.adyeshach.api.event.AdyeshachEntityCreateEvent
import ink.ptms.adyeshach.api.event.AdyeshachEntityTeleportEvent
import ink.ptms.adyeshach.api.event.AdyeshachEntityVisibleEvent
import ink.ptms.adyeshach.api.event.AdyeshachHeadRotationEvent
import ink.ptms.adyeshach.common.entity.type.AdyHuman
import ink.ptms.adyeshach.common.entity.type.AdyItem
import ink.ptms.adyeshach.common.entity.type.AdyMinecart
import ink.ptms.adyeshach.common.entity.type.AdyWitherSkull
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.common.util.Vector

object Patch {

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private fun onTeleport(e: AdyeshachEntityTeleportEvent) {
        e.entity.getPassengers().forEach {
            it.teleport(e.location.clone().add(1.5, 0.0, 1.5))
        }
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private fun onRotation(e: AdyeshachHeadRotationEvent) {
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
    private fun onCreate(e: AdyeshachEntityCreateEvent) {
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
    private fun onVisible(e: AdyeshachEntityVisibleEvent) {
        if (e.visible) {
            when (e.entity) {
                // 修复掉落物品显示错误问题
                is AdyItem -> {
                    e.entity.setMetadata("item", e.entity.getItem())
                    e.entity.sendVelocity(Vector(0, 0, 0))
                }
                // 修复玩家类型视角和装备无法正常显示的问题
                is AdyHuman -> {
                    submit(delay = 1) {
                        e.entity.setHeadRotation(e.entity.yaw, e.entity.pitch, true)
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
                    submit(delay = 5) { e.entity.setHeadRotation(e.entity.yaw, e.entity.pitch, true) }
                }
            }
        }
    }
}