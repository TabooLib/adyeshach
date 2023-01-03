package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityEquipable
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.AdyEntityLiving
import org.bukkit.Color
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

/**
 * @author sky
 * @since 2020-08-04 18:28
 */
@Deprecated("Outdated but usable")
open class AdyEntityLiving(entityTypes: EntityTypes, v2: ink.ptms.adyeshach.core.entity.EntityInstance) : AdyEntity(entityTypes, v2), EntityEquipable {

    var isHandActive: Boolean
        get() = getMetadata("isHandActive")
        set(value) {
            setMetadata("isHandActive", value)
        }

    var activeHand: Boolean
        get() = getMetadata("activeHand")
        set(value) {
            setMetadata("activeHand", value)
        }

    var isInRiptideSpinAttack: Boolean
        get() = getMetadata("isInRiptideSpinAttack")
        set(value) {
            setMetadata("isInRiptideSpinAttack", value)
        }

    var arrowsInEntity: Int
        get() = getMetadata("arrowsInEntity")
        set(value) {
            setMetadata("arrowsInEntity", value)
        }

    var beeStingersInEntity: Int
        get() = getMetadata("beeStingersInEntity")
        set(value) {
            setMetadata("beeStingersInEntity", value)
        }

    override fun visible(viewer: Player, visible: Boolean): Boolean {
        return v2.visible(viewer, visible)
    }

    override fun getEquipment(): Map<EquipmentSlot, ItemStack> {
        v2 as AdyEntityLiving
        return v2.getEquipment()
    }

    override fun setItemInMainHand(itemStack: ItemStack) {
        v2 as AdyEntityLiving
        v2.setItemInMainHand(itemStack)
    }

    override fun getItemInMainHand(): ItemStack? {
        v2 as AdyEntityLiving
        return v2.getItemInMainHand()
    }

    override fun setItemInOffHand(itemStack: ItemStack) {
        v2 as AdyEntityLiving
        v2.setItemInOffHand(itemStack)
    }

    override fun getItemInOffHand(): ItemStack? {
        v2 as AdyEntityLiving
        return v2.getItemInOffHand()
    }

    override fun setHelmet(itemStack: ItemStack) {
        v2 as AdyEntityLiving
        v2.setHelmet(itemStack)
    }

    override fun getHelmet(): ItemStack? {
        v2 as AdyEntityLiving
        return v2.getHelmet()
    }

    override fun setChestplate(itemStack: ItemStack) {
        v2 as AdyEntityLiving
        v2.setChestplate(itemStack)
    }

    override fun getChestplate(): ItemStack? {
        v2 as AdyEntityLiving
        return v2.getChestplate()
    }

    override fun setLeggings(itemStack: ItemStack) {
        v2 as AdyEntityLiving
        v2.setLeggings(itemStack)
    }

    override fun getLeggings(): ItemStack? {
        v2 as AdyEntityLiving
        return v2.getLeggings()
    }

    override fun setBoots(itemStack: ItemStack) {
        v2 as AdyEntityLiving
        v2.setBoots(itemStack)
    }

    override fun getBoots(): ItemStack? {
        v2 as AdyEntityLiving
        return v2.getBoots()
    }

    override fun clear() {
        v2 as AdyEntityLiving
        v2.clearEquipment()
    }

    fun setEquipment(equipmentSlot: EquipmentSlot, itemStack: ItemStack) {
        v2 as AdyEntityLiving
        v2.setEquipment(equipmentSlot, itemStack)
    }

    fun getEquipment(equipmentSlot: EquipmentSlot): ItemStack? {
        v2 as AdyEntityLiving
        return v2.getEquipment(equipmentSlot)
    }

    fun updateEquipment() {
        v2 as AdyEntityLiving
        v2.updateEquipment()
    }

    fun die(die: Boolean = true) {
        v2 as AdyEntityLiving
        v2.die(die)
    }

    fun die(viewer: Player, die: Boolean = true) {
        v2 as AdyEntityLiving
        v2.die(viewer, die)
    }

    fun setHealth(value: Float) {
        setMetadata("health", value)
    }

    fun getHealth(): Float {
        return getMetadata("health")
    }

    fun setPotionEffectColor(value: Color) {
        setMetadata("potionEffectColor", value.asRGB())
    }

    fun getPotionEffectColor(): Color {
        return Color.fromRGB(getMetadata<Int>("potionEffectColor").let { if (it == -1) 0 else it })
    }
}