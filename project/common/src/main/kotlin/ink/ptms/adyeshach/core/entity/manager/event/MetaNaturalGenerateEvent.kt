package ink.ptms.adyeshach.core.entity.manager.event

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.MetaNatural
import org.bukkit.entity.Player

/**
 * @author sky
 * @since 2020-08-14 19:21
 */
class MetaNaturalGenerateEvent(val entity: EntityInstance, val player: Player, val meta: MetaNatural<Any, out EntityInstance>, var value: Any)