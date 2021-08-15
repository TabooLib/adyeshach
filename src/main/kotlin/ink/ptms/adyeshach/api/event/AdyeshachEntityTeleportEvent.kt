package ink.ptms.adyeshach.api.event

import ink.ptms.adyeshach.common.entity.EntityInstance
import org.bukkit.Location
import taboolib.platform.type.BukkitProxyEvent

/**
 * @Author sky
 * @Since 2020-08-14 19:21
 */
class AdyeshachEntityTeleportEvent(val entity: EntityInstance, var location: Location) : BukkitProxyEvent()