package ink.ptms.adyeshach.api.event

import ink.ptms.adyeshach.common.bukkit.data.GameProfile
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityMetaable
import ink.ptms.adyeshach.common.entity.type.AdyHuman
import io.izzel.taboolib.module.event.EventNormal
import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-14 19:21
 */
class AdyeshachGameProfileGenerateEvent(val entity: AdyHuman, val player: Player, var gameProfile: GameProfile) : EventNormal<AdyeshachGameProfileGenerateEvent>() {

    init {
        async(!Bukkit.isPrimaryThread())
    }
}