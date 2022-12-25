package ink.ptms.adyeshach.core.event

import org.bukkit.inventory.ItemStack
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author sky
 * @since 2020-08-14 19:21
 */
class AdyeshachItemHookEvent(val namespace: String, val source: String, var itemStack: ItemStack) : BukkitProxyEvent() {

    override val allowCancelled: Boolean
        get() = false
}