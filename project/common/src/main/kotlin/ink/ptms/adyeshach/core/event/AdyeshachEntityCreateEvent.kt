package ink.ptms.adyeshach.core.event

import ink.ptms.adyeshach.core.entity.EntityInstance
import org.bukkit.Location
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author sky
 * @since 2020-08-14 19:21
 */
class AdyeshachEntityCreateEvent(val entity: EntityInstance, var location: Location) : BukkitProxyEvent()