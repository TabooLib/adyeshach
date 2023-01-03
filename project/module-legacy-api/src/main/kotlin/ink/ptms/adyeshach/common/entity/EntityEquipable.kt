package ink.ptms.adyeshach.common.entity

import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

/**
 * @author sky
 * @since 2020-08-04 15:30
 */
@Deprecated("Outdated but usable")
interface EntityEquipable {

    fun getEquipment(): Map<EquipmentSlot, ItemStack>

    fun setItemInMainHand(itemStack: ItemStack)

    fun getItemInMainHand(): ItemStack?

    fun setItemInOffHand(itemStack: ItemStack)

    fun getItemInOffHand(): ItemStack?

    fun setHelmet(itemStack: ItemStack)

    fun getHelmet(): ItemStack?

    fun setChestplate(itemStack: ItemStack)

    fun getChestplate(): ItemStack?

    fun setLeggings(itemStack: ItemStack)

    fun getLeggings(): ItemStack?

    fun setBoots(itemStack: ItemStack)

    fun getBoots(): ItemStack?

    fun clear()
}