package ink.ptms.adyeshach.api.event

import ink.ptms.adyeshach.common.entity.EntityMetaable
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author sky
 * @since 2020-08-14 19:21
 */
class AdyeshachTagUpdateEvent(val entity: EntityMetaable, val key: String, var value: Any?) : BukkitProxyEvent()