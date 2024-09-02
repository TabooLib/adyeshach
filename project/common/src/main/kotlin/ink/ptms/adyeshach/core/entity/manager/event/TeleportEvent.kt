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
class TeleportEvent(val entity: EntityInstance, val location: Location) {

    /** 是否发生实质性的位置变动 */
    var isPositionChanged = isChanged(entity.getLocation(), location)

    companion object {

        fun isChanged(l1: Location, l2: Location): Boolean {
            return l1.x != l2.x || l1.y != l2.y || l1.z != l2.z
        }
    }
}