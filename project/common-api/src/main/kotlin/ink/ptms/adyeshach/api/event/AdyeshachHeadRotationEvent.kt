package ink.ptms.adyeshach.api.event

import ink.ptms.adyeshach.common.entity.EntityInstance
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author sky
 * @since 2020-08-14 19:21
 */
@Deprecated("不再触发，改为 AdyeshachEntityTeleportEvent")
class AdyeshachHeadRotationEvent(val entity: EntityInstance, var yaw: Float, var pitch: Float) : BukkitProxyEvent()