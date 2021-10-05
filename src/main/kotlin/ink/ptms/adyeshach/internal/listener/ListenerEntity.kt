package ink.ptms.adyeshach.internal.listener

import com.ticxo.modelengine.api.ModelEngineAPI
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.event.*
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.EntityTypes
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.warning
import taboolib.common.reflect.Reflex.Companion.getProperty
import taboolib.common.util.Vector
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.PacketReceiveEvent

/**
 * @author sky
 * @since 2020-08-15 15:53
 */
internal object ListenerEntity {

    @SubscribeEvent
    fun e(e: PacketReceiveEvent) {
        if (e.packet.name == "PacketPlayInPosition" && e.player.name !in AdyeshachAPI.onlinePlayerMap) {
            AdyeshachAPI.onlinePlayerMap.add(e.player.name)
            AdyeshachPlayerJoinEvent(e.player).call()
        }
        if (e.packet.name == "PacketPlayInUseEntity") {
            val entity = AdyeshachAPI.getEntityFromEntityId(e.packet.read("a")!!, e.player) ?: return
            // 判定观察者并检测作弊
            if (entity.isViewer(e.player) && entity.getWorld() == e.player.world && entity.getLocation().distance(e.player.location) < 10) {
                if (MinecraftVersion.isUniversal) {
                    val action = e.packet.read<Any>("action")!!
                    when (action.javaClass.simpleName) {
                        // ATTACK
                        "d" -> {
                            submit {
                                AdyeshachEntityDamageEvent(entity, e.player).call()
                            }
                        }
                        // INTERACT_AT
                        "e" -> {
                            val location = action.getProperty<Any>("location")
                            submit {
                                AdyeshachEntityInteractEvent(
                                    entity,
                                    e.player,
                                    action.getProperty<Any>("hand").toString() == "MAIN_HAND",
                                    if (location == null) Vector(0, 0, 0) else NMS.INSTANCE.parseVec3d(location)
                                ).call()
                            }
                        }
                    }
                } else {
                    when (e.packet.read<Any>("action").toString()) {
                        "ATTACK" -> {
                            submit {
                                AdyeshachEntityDamageEvent(entity, e.player).call()
                            }
                        }
                        "INTERACT_AT" -> {
                            val location = e.packet.read<Any>("c")
                            submit {
                                AdyeshachEntityInteractEvent(
                                    entity,
                                    e.player,
                                    e.packet.read<Any>("d").toString() == "MAIN_HAND",
                                    if (location == null) Vector(0, 0, 0) else NMS.INSTANCE.parseVec3d(location)
                                ).call()
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: AdyeshachEntityTeleportEvent) {
        e.entity.getPassengers().forEach {
            it.teleport(e.location.clone().add(1.5, 0.0, 1.5))
        }
    }

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