package ink.ptms.adyeshach.common.util.serializer

import org.bukkit.inventory.ItemStack

@Deprecated("Outdated but usable")
object Serializer {

    val gson = ink.ptms.adyeshach.core.serializer.Serializer.gson

    fun toItemStack(data: String): ItemStack {
        return ink.ptms.adyeshach.core.serializer.Serializer.toItemStack(data)
    }

    fun fromItemStack(itemStack: ItemStack): String {
        return ink.ptms.adyeshach.core.serializer.Serializer.fromItemStack(itemStack)
    }
}