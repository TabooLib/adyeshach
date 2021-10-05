package ink.ptms.adyeshach.api.event

import ink.ptms.adyeshach.common.entity.EntityInstance
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author sky
 * @since 2020-08-14 19:21
 */
class AdyeshachEntitySpawnEvent(val entity: EntityInstance) : BukkitProxyEvent() {

    override val allowCancelled: Boolean
        get() = false
}