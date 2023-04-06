package ink.ptms.adyeshach.core.entity.type

import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.TextDisplay
import org.bukkit.inventory.ItemStack
import taboolib.module.chat.ComponentText

/**
 * @author sky
 * @date 2023/4/5 22:18
 */
interface AdyItemDisplay : AdyDisplay {

    fun setDisplayedItem(value: ItemStack) {
        setMetadata("displayedItem", value)
    }

    fun getDisplayedItem(): ItemStack {
        return getMetadata("displayedItem")
    }

    fun setDisplayType(value: Byte) {
        setMetadata("displayType", value)
    }

    fun setDisplayType(value: ItemDisplay.ItemDisplayTransform) {
        setMetadata("displayType", value.ordinal.toByte())
    }

    fun getDisplayType(): Byte {
        return getMetadata("displayType")
    }
}