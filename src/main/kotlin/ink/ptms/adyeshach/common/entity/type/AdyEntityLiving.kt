package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.editor.Editors
import ink.ptms.adyeshach.common.entity.EntityEquipable
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.util.BukkitUtils
import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.Color
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * @Author sky
 * @Since 2020-08-04 18:28
 */
open class AdyEntityLiving(entityTypes: EntityTypes) : AdyEntity(entityTypes), EntityEquipable {

    @Expose
    protected val equipment = HashMap<EquipmentSlot, ItemStack>()

    init {
        registerMeta(at(11400 to 9, 11000 to 8, 10900 to 7), "potionEffectColor", 0)
                .from(Editors.COLOR)
                .build()
        registerEditor("equipmentHelmet")
                .from(Editors.equip(EquipmentSlot.HEAD))
                .build()
        registerEditor("equipmentChestplate")
                .from(Editors.equip(EquipmentSlot.CHEST))
                .build()
        registerEditor("equipmentLeggings")
                .from(Editors.equip(EquipmentSlot.LEGS))
                .build()
        registerEditor("equipmentBoots")
                .from(Editors.equip(EquipmentSlot.FEET))
                .build()
        registerEditor("equipmentHand")
                .from(Editors.equip(EquipmentSlot.HAND))
                .build()
        registerEditor("equipmentOffhand")
                .from(Editors.equip(EquipmentSlot.OFF_HAND))
                .build()
    }

    override fun visible(viewer: Player, visible: Boolean) {
        if (visible) {
            spawn(viewer) {
                NMS.INSTANCE.spawnEntityLiving(viewer, entityType.getEntityTypeNMS(), index, UUID.randomUUID(), position.toLocation())
            }
        } else {
            destroy(viewer) {
                NMS.INSTANCE.destroyEntity(viewer, index)
            }
        }
    }

    override fun getEquipment(): Map<EquipmentSlot, ItemStack> {
        return HashMap(equipment)
    }

    override fun setItemInMainHand(itemStack: ItemStack) {
        equipment[EquipmentSlot.HAND] = itemStack
        forViewers {
            NMS.INSTANCE.updateEquipment(it, index, EquipmentSlot.HAND, itemStack)
        }
    }

    override fun getItemInMainHand(): ItemStack? {
        return equipment[EquipmentSlot.HAND]
    }

    override fun setItemInOffHand(itemStack: ItemStack) {
        equipment[EquipmentSlot.OFF_HAND] = itemStack
        forViewers {
            NMS.INSTANCE.updateEquipment(it, index, EquipmentSlot.OFF_HAND, itemStack)
        }
    }

    override fun getItemInOffHand(): ItemStack? {
        return equipment[EquipmentSlot.OFF_HAND]
    }

    override fun setHelmet(itemStack: ItemStack) {
        equipment[EquipmentSlot.HEAD] = itemStack
        forViewers {
            NMS.INSTANCE.updateEquipment(it, index, EquipmentSlot.HEAD, itemStack)
        }
    }

    override fun getHelmet(): ItemStack? {
        return equipment[EquipmentSlot.HEAD]
    }

    override fun setChestplate(itemStack: ItemStack) {
        equipment[EquipmentSlot.CHEST] = itemStack
        forViewers {
            NMS.INSTANCE.updateEquipment(it, index, EquipmentSlot.CHEST, itemStack)
        }
    }

    override fun getChestplate(): ItemStack? {
        return equipment[EquipmentSlot.CHEST]
    }

    override fun setLeggings(itemStack: ItemStack) {
        equipment[EquipmentSlot.LEGS] = itemStack
        forViewers {
            NMS.INSTANCE.updateEquipment(it, index, EquipmentSlot.LEGS, itemStack)
        }
    }

    override fun getLeggings(): ItemStack? {
        return equipment[EquipmentSlot.LEGS]
    }

    override fun setBoots(itemStack: ItemStack) {
        equipment[EquipmentSlot.FEET] = itemStack
        forViewers {
            NMS.INSTANCE.updateEquipment(it, index, EquipmentSlot.FEET, itemStack)
        }
    }

    override fun getBoots(): ItemStack? {
        return equipment[EquipmentSlot.FEET]
    }

    override fun clear() {
        equipment.clear()
    }

    fun setPotionEffectColor(value: Color) {
        setMetadata("potionEffectColor", value.asRGB())
    }

    fun getPotionEffectColor(): Color {
        return Color.fromRGB(getMetadata("potionEffectColor"))
    }

    fun updateEquipment() {
        forViewers {
            BukkitUtils.valuesEquipmentSlot().forEach { equipment ->
                NMS.INSTANCE.updateEquipment(it, index, equipment, getEquipment(equipment) ?: return@forEach)
            }
        }
    }

    fun setEquipment(equipmentSlot: EquipmentSlot, itemStack: ItemStack) {
        when (equipmentSlot) {
            EquipmentSlot.HAND -> setItemInMainHand(itemStack)
            EquipmentSlot.OFF_HAND -> setItemInOffHand(itemStack)
            EquipmentSlot.FEET -> setBoots(itemStack)
            EquipmentSlot.LEGS -> setLeggings(itemStack)
            EquipmentSlot.CHEST -> setChestplate(itemStack)
            EquipmentSlot.HEAD -> setHelmet(itemStack)
        }
    }

    fun getEquipment(equipmentSlot: EquipmentSlot): ItemStack? {
        return when (equipmentSlot) {
            EquipmentSlot.HAND -> getItemInMainHand()
            EquipmentSlot.OFF_HAND -> getItemInOffHand()
            EquipmentSlot.FEET -> getBoots()
            EquipmentSlot.LEGS -> getLeggings()
            EquipmentSlot.CHEST -> getChestplate()
            EquipmentSlot.HEAD -> getHelmet()
        }
    }
}