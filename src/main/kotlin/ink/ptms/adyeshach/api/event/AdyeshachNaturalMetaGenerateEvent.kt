package ink.ptms.adyeshach.api.event

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityMetaable
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

/**
 * @Author sky
 * @Since 2020-08-14 19:21
 */
class AdyeshachNaturalMetaGenerateEvent(
    val entity: EntityInstance,
    val player: Player,
    val meta: EntityMetaable.MetaNatural<*>,
    var value: Any,
) : BukkitProxyEvent() {

    override val allowCancelled: Boolean
        get() = false
}