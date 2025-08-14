package ink.ptms.adyeshach.core.event

import ink.ptms.adyeshach.core.entity.EntityInstance
import taboolib.platform.type.BukkitProxyEvent

/**
 * 管理器加载时触发
 */
class AdyeshachEntityLoadedEvent(val entity: EntityInstance) : BukkitProxyEvent()