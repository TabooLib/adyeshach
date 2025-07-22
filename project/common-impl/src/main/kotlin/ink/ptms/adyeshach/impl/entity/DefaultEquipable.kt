package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityEquipable
import ink.ptms.adyeshach.core.event.AdyeshachEntityEquipmentUpdateEvent
import ink.ptms.adyeshach.impl.entity.type.DefaultEntityLiving
import org.bukkit.Material
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
        this as DefaultEntityLiving
        equipment.clear()
        updateEquipment()
    }

    override fun updateEquipment() {
        this as DefaultEntityLiving
        val operator = Adyeshach.api().getMinecraftAPI().getEntityOperator()
        val players = getVisiblePlayers()
        val equipment = EquipmentSlot.values().associateWith { getEquipment(it) ?: ItemStack(Material.AIR) }.toMutableMap()
        AdyeshachEntityEquipmentUpdateEvent(players, this, equipment).call()
        operator.updateEquipment(players, index, equipment)
    }

    override fun updateEquipment(player: Player) {
        this as DefaultEntityLiving
        val operator = Adyeshach.api().getMinecraftAPI().getEntityOperator()
        val equipment = EquipmentSlot.values().associateWith { getEquipment(it) ?: ItemStack(Material.AIR) }.toMutableMap()
        AdyeshachEntityEquipmentUpdateEvent(listOf(player), this, equipment).call()
        operator.updateEquipment(player, index, equipment)
    }

    override fun getEquipment(): Map<EquipmentSlot, ItemStack> {
        this as DefaultEntityLiving
        return equipment
    }

    override fun getEquipment(equipmentSlot: EquipmentSlot): ItemStack? {
        this as DefaultEntityLiving
        return equipment[equipmentSlot]
    }

    override fun setEquipment(equipmentSlot: EquipmentSlot, itemStack: ItemStack) {
        this as DefaultEntityLiving
        equipment[equipmentSlot] = itemStack
        updateEquipment()
    }

    override fun setItemInMainHand(itemStack: ItemStack) {
        this as DefaultEntityLiving
        equipment[EquipmentSlot.HAND] = itemStack
        updateEquipment()
    }

    override fun getItemInMainHand(): ItemStack? {
        this as DefaultEntityLiving
        return equipment[EquipmentSlot.HAND]
    }

    override fun setItemInOffHand(itemStack: ItemStack) {
        this as DefaultEntityLiving
        equipment[EquipmentSlot.OFF_HAND] = itemStack
        updateEquipment()
    }

    override fun getItemInOffHand(): ItemStack? {
        this as DefaultEntityLiving
        return equipment[EquipmentSlot.OFF_HAND]
    }

    override fun setHelmet(itemStack: ItemStack) {
        this as DefaultEntityLiving
        equipment[EquipmentSlot.HEAD] = itemStack
        updateEquipment()
    }

    override fun getHelmet(): ItemStack? {
        this as DefaultEntityLiving
        return equipment[EquipmentSlot.HEAD]
    }

    override fun setChestplate(itemStack: ItemStack) {
        this as DefaultEntityLiving
        equipment[EquipmentSlot.CHEST] = itemStack
        updateEquipment()
    }

    override fun getChestplate(): ItemStack? {
        this as DefaultEntityLiving
        return equipment[EquipmentSlot.CHEST]
    }

    override fun setLeggings(itemStack: ItemStack) {
        this as DefaultEntityLiving
        equipment[EquipmentSlot.LEGS] = itemStack
        updateEquipment()
    }

    override fun getLeggings(): ItemStack? {
        this as DefaultEntityLiving
        return equipment[EquipmentSlot.LEGS]
    }

    override fun setBoots(itemStack: ItemStack) {
        this as DefaultEntityLiving
        equipment[EquipmentSlot.FEET] = itemStack
    }

    override fun getBoots(): ItemStack? {
        this as DefaultEntityLiving
        return equipment[EquipmentSlot.FEET]
    }
}