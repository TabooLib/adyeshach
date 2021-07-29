package ink.ptms.adyeshach.api.event

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityMetaable
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyEvent

/**
 * @Author sky
 * @Since 2020-08-14 19:21
 */
class AdyeshachMaskedMetaGenerateEvent(
    val entity: EntityInstance,
    val player: Player,
    val meta: EntityMetaable.MetaMasked,
    val byteMask: MutableMap<EntityMetaable.MetaMasked, Boolean>,
) : ProxyEvent() {

    override val allowCancelled: Boolean
        get() = false
}