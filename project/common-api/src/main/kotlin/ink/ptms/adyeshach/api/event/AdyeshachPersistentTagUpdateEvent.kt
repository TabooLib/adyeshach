package ink.ptms.adyeshach.api.event

import ink.ptms.adyeshach.common.entity.EntityInstance
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author sky
 * @since 2020-08-14 19:21
 */
class AdyeshachPersistentTagUpdateEvent(val entity: EntityInstance, val key: String, var value: String?) : BukkitProxyEvent()