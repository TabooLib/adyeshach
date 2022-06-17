package ink.ptms.adyeshach.api.event

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.Meta
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author sky
 * @since 2020-08-14 19:21
 */
class AdyeshachMetaUpdateEvent(val entity: EntityInstance, val meta: Meta<*>, val key: String, var value: Any) : BukkitProxyEvent()