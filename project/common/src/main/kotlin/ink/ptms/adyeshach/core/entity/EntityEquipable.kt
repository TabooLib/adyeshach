package ink.ptms.adyeshach.core.entity

import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

/**
 * @author sky
 * @since 2020-08-04 15:30
 */
@Suppress("SpellCheckingInspection")
interface EntityEquipable {

    /**
     * 清空装备
     */
    fun clearEquipment()

    /**
     * 更新装备信息
     */
    fun updateEquipment()

    /**
     * 向给定玩家更新装备信息
     */
    fun updateEquipment(player: Player)

    /**
     * 获取所有装备
     */
    fun getEquipment(): Map<EquipmentSlot, ItemStack>

    /**
     * 获取装备
     */
    fun getEquipment(equipmentSlot: EquipmentSlot): ItemStack?

    /**
     * 设置装备
     */
    fun setEquipment(equipmentSlot: EquipmentSlot, itemStack: ItemStack)

    /**
     * 设置手中物品
     */
    fun setItemInMainHand(itemStack: ItemStack)

    /**
     * 获取手中物品
     */
    fun getItemInMainHand(): ItemStack?

    /**
     * 设置副手物品
     */
    fun setItemInOffHand(itemStack: ItemStack)

    /**
     * 获取副手物品
     */
    fun getItemInOffHand(): ItemStack?

    /**
     * 设置头部物品
     */
    fun setHelmet(itemStack: ItemStack)

    /**
     * 获取头部物品
     */
    fun getHelmet(): ItemStack?

    /**
     * 设置胸部物品
     */
    fun setChestplate(itemStack: ItemStack)

    /**
     * 获取胸部物品
     */
    fun getChestplate(): ItemStack?

    /**
     * 设置腿部物品
     */
    fun setLeggings(itemStack: ItemStack)

    /**
     * 获取腿部物品
     */
    fun getLeggings(): ItemStack?

    /**
     * 设置脚部物品
     */
    fun setBoots(itemStack: ItemStack)

    /**
     * 获取脚部物品
     */
    fun getBoots(): ItemStack?
}