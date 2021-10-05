package ink.ptms.adyeshach.api.event

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityMetaable
import ink.ptms.adyeshach.common.entity.MetaNatural
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author sky
 * @since 2020-08-14 19:21
 */
class AdyeshachNaturalMetaGenerateEvent(
    val entity: EntityInstance,
    val player: Player,
    val meta: MetaNatural<*>,
    var value: Any,
) : BukkitProxyEvent() {

    override val allowCancelled: Boolean
        get() = false
}