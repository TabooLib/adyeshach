package ink.ptms.adyeshach.core.entity.manager.event

import ink.ptms.adyeshach.core.entity.EntityInstance
import org.bukkit.Location

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.manager.callback.TeleportEvent
 *
 * @author 坏黑
 * @since 2022/12/13 21:33
 */
class TeleportEvent(val entity: EntityInstance, val location: Location)