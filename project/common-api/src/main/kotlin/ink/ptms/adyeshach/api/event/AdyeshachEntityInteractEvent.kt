package ink.ptms.adyeshach.api.event

import ink.ptms.adyeshach.common.entity.EntityInstance
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author sky
 * @since 2020-08-14 19:21
 */
class AdyeshachEntityInteractEvent(val entity: EntityInstance, val player: Player, var isMainHand: Boolean, var vector: Vector) : BukkitProxyEvent()