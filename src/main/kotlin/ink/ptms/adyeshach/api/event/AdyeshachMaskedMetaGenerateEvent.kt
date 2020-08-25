package ink.ptms.adyeshach.api.event

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityMetaable
import io.izzel.taboolib.module.event.EventNormal
import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-14 19:21
 */
class AdyeshachMaskedMetaGenerateEvent(val entity: EntityInstance, val player: Player, val meta: EntityMetaable.MetaMasked, val byteMask: MutableMap<EntityMetaable.MetaMasked, Boolean>) : EventNormal<AdyeshachMaskedMetaGenerateEvent>() {

    init {
        async(!Bukkit.isPrimaryThread())
    }
}