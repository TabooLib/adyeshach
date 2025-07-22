package ink.ptms.adyeshach.core.event

import ink.ptms.adyeshach.core.entity.EntityInstance
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class AdyeshachPlayerJoinVisualTeamEvent(
    val player: Player,
    val entity: EntityInstance,
    val nameTagVisible: Boolean,
    val collision: Boolean,
    val color: ChatColor,
    val canSeeInvisible: Boolean,
) : BukkitProxyEvent()