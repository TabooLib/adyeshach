package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.common.entity.EntityEquipable
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.DefaultEquipable
 *
 * @author 坏黑
 * @since 2022/6/20 00:43
 */
@Suppress("SpellCheckingInspection")
interface DefaultEquipable : EntityEquipable {

    override fun clearEquipment() {
        TODO("Not yet implemented")
    }

    override fun updateEquipment() {
        TODO("Not yet implemented")
    }

    override fun updateEquipment(player: Player) {
        TODO("Not yet implemented")
    }

    override fun getEquipment(): Map<EquipmentSlot, ItemStack> {
        TODO("Not yet implemented")
    }

    override fun getEquipment(equipmentSlot: EquipmentSlot): ItemStack? {
        TODO("Not yet implemented")
    }

    override fun setEquipment(equipmentSlot: EquipmentSlot, itemStack: ItemStack) {
        TODO("Not yet implemented")
    }

    override fun setItemInMainHand(itemStack: ItemStack) {
        TODO("Not yet implemented")
    }

    override fun getItemInMainHand(): ItemStack? {
        TODO("Not yet implemented")
    }

    override fun setItemInOffHand(itemStack: ItemStack) {
        TODO("Not yet implemented")
    }

    override fun getItemInOffHand(): ItemStack? {
        TODO("Not yet implemented")
    }

    override fun setHelmet(itemStack: ItemStack) {
        TODO("Not yet implemented")
    }

    override fun getHelmet(): ItemStack? {
        TODO("Not yet implemented")
    }

    override fun setChestplate(itemStack: ItemStack) {
        TODO("Not yet implemented")
    }

    override fun getChestplate(): ItemStack? {
        TODO("Not yet implemented")
    }

    override fun setLeggings(itemStack: ItemStack) {
        TODO("Not yet implemented")
    }

    override fun getLeggings(): ItemStack? {
        TODO("Not yet implemented")
    }

    override fun setBoots(itemStack: ItemStack) {
        TODO("Not yet implemented")
    }

    override fun getBoots(): ItemStack? {
        TODO("Not yet implemented")
    }
}