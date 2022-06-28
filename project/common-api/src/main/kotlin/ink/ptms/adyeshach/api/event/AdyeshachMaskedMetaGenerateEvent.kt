package ink.ptms.adyeshach.api.event

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.MetaMasked
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author sky
 * @since 2020-08-14 19:21
 */
class AdyeshachMaskedMetaGenerateEvent(
    val entity: EntityInstance,
    val player: Player,
    val meta: MetaMasked<*>,
    val byteMask: MutableMap<MetaMasked<*>, Boolean>,
) : BukkitProxyEvent()