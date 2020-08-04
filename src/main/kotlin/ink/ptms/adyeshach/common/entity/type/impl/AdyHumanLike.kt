package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.EntityEquipable
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.nms.NMS
import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.HashMap

/**
 * @Author sky
 * @Since 2020-08-04 13:40
 */
open class AdyHumanLike(owner: Player, entityTypes: EntityTypes) : EntityInstance(owner, entityTypes), EntityEquipable {

    @Expose
    private val equipment = HashMap<EquipmentSlot, ItemStack>()

    open fun isLiving(): Boolean = true

    override fun spawn(location: Location) {
        super.spawn(location)
        if (isLiving()) {
            NMS.INSTANCE.spawnEntityLiving(owner, entityType.getEntityTypeNMS(), index, UUID.randomUUID(), location)
        } else {
            NMS.INSTANCE.spawnEntity(owner, entityType.getEntityTypeNMS(), index, UUID.randomUUID(), location)
        }
    }

    override fun getEquipment(): Map<EquipmentSlot, ItemStack> {
        return HashMap(equipment)
    }

    override fun setItemInMainHand(itemStack: ItemStack) {
        equipment[EquipmentSlot.HAND] = itemStack
        NMS.INSTANCE.updateEquipment(owner, index, EquipmentSlot.HAND, itemStack)
    }

    override fun getItemInMainHand(): ItemStack? {
        return equipment[EquipmentSlot.HAND]
    }

    override fun setItemInOffHand(itemStack: ItemStack) {
        equipment[EquipmentSlot.OFF_HAND] = itemStack
        NMS.INSTANCE.updateEquipment(owner, index, EquipmentSlot.OFF_HAND, itemStack)
    }

    override fun getItemInOffHand(): ItemStack? {
        return equipment[EquipmentSlot.OFF_HAND]
    }

    override fun setHelmet(itemStack: ItemStack) {
        equipment[EquipmentSlot.HEAD] = itemStack
        NMS.INSTANCE.updateEquipment(owner, index, EquipmentSlot.HEAD, itemStack)
    }

    override fun getHelmet(): ItemStack? {
        return equipment[EquipmentSlot.HEAD]
    }

    override fun setChestplate(itemStack: ItemStack) {
        equipment[EquipmentSlot.CHEST] = itemStack
        NMS.INSTANCE.updateEquipment(owner, index, EquipmentSlot.CHEST, itemStack)
    }

    override fun getChestplate(): ItemStack? {
        return equipment[EquipmentSlot.CHEST]
    }

    override fun setLeggings(itemStack: ItemStack) {
        equipment[EquipmentSlot.LEGS] = itemStack
        NMS.INSTANCE.updateEquipment(owner, index, EquipmentSlot.LEGS, itemStack)
    }

    override fun getLeggings(): ItemStack? {
        return equipment[EquipmentSlot.LEGS]
    }

    override fun setBoots(itemStack: ItemStack) {
        equipment[EquipmentSlot.FEET] = itemStack
        NMS.INSTANCE.updateEquipment(owner, index, EquipmentSlot.FEET, itemStack)
    }

    override fun getBoots(): ItemStack? {
        return equipment[EquipmentSlot.FEET]
    }

    override fun clear() {
        equipment.clear()
    }
}