package ink.ptms.adyeshach.api.event

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityMetaable
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

/**
 * @Author sky
 * @Since 2020-08-14 19:21
 */
class AdyeshachMetaUpdateEvent(
    val entity: EntityMetaable,
    val meta: EntityMetaable.Meta,
    val key: String,
    var value: Any,
) : BukkitProxyEvent()