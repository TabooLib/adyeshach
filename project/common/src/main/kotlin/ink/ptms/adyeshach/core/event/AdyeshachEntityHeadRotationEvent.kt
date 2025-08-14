package ink.ptms.adyeshach.core.event

import ink.ptms.adyeshach.core.entity.EntityInstance
import taboolib.platform.type.BukkitProxyEvent

class AdyeshachEntityHeadRotationEvent(val entity: EntityInstance, val yaw: Float, val pitch: Float, val forceUpdate: Boolean): BukkitProxyEvent()