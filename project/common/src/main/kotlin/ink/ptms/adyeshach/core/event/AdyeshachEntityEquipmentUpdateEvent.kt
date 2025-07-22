package ink.ptms.adyeshach.core.event

import ink.ptms.adyeshach.core.entity.type.AdyEntityLiving
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import taboolib.platform.type.BukkitProxyEvent

/**
 * NPC 的装备发送给玩家时
 */
class AdyeshachEntityEquipmentUpdateEvent(
    val player: List<Player>,
    val entity: AdyEntityLiving,
    val equipment: MutableMap<EquipmentSlot, ItemStack>,
) : BukkitProxyEvent()