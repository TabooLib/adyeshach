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
class AdyeshachNaturalMetaGenerateEvent(val entity: EntityInstance, val player: Player, val meta: EntityMetaable.MetaNatural<*>, var value: Any) : EventNormal<AdyeshachNaturalMetaGenerateEvent>() {

    init {
        async(!Bukkit.isPrimaryThread())
    }
}