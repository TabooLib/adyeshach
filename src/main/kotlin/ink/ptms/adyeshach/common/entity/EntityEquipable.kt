package ink.ptms.adyeshach.common.entity

import ink.ptms.adyeshach.common.entity.EntityBase
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.type.EntityTypes
import io.izzel.taboolib.internal.gson.annotations.Expose
import io.izzel.taboolib.module.lite.SimpleEquip
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

/**
 * @Author sky
 * @Since 2020-08-04 15:30
 */
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

    fun clear();
}