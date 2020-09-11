package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.bukkit.data.DataWatcher
import ink.ptms.adyeshach.common.editor.Editor
import ink.ptms.adyeshach.common.editor.Editor.toDisplay
import ink.ptms.adyeshach.common.editor.Editors
import ink.ptms.adyeshach.common.entity.EntityEquipable
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.util.BukkitUtils
import ink.ptms.adyeshach.common.util.Tasks
import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.Color
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.util.NumberConversions
import java.util.*

/**
 * @Author sky
 * @Since 2020-08-04 18:28
 */
open class AdyEntityLiving(entityTypes: EntityTypes) : AdyEntity(entityTypes), EntityEquipable {

    @Expose
    protected val equipment = HashMap<EquipmentSlot, ItemStack>()
    @Expose
    protected var isDie = false

    init {
        registerMeta(at(11400 to 8, 11000 to 7, 10900 to 6), "health", 1.0f)
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
        registerEditor("isDie")
                .reset { entity, meta ->
                    die(die = false)
                }
                .modify { player, entity, meta ->
                    die(die = !isDie)
                    Editor.open(player, entity)
                }
                .display { _, entity, meta ->
                    isDie.toDisplay()
                }
    }

    override fun visible(viewer: Player, visible: Boolean) {
        if (visible) {
            spawn(viewer) {
                NMS.INSTANCE.spawnEntityLiving(viewer, entityType, index, UUID.randomUUID(), position.toLocation())
            }
            Tasks.delay(1) {
                updateEquipment()
            }
            Tasks.delay(5) {
                if (isDie) {
                    die(viewer = viewer)
                }
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

    /**
     * 尸体状态
     */
    fun die(die: Boolean = true) {
        isDie = die
        if (isDie) {
            setHealth(-1f)
            Tasks.delay(15) {
                if (isDie) {
                    setHealth(1f)
                }
            }
        } else {
            respawn()
        }
    }

    /**
     * 对指定玩家播放尸体状态
     */
    fun die(viewer: Player, die: Boolean = true) {
        if (die) {
            val registerMeta = meta.firstOrNull { it.key == "health" }!!
            NMS.INSTANCE.updateEntityMetadata(viewer, index, NMS.INSTANCE.getMetaEntityFloat(registerMeta.index, NumberConversions.toFloat(-1)))
            Tasks.delay(15) {
                NMS.INSTANCE.updateEntityMetadata(viewer, index, NMS.INSTANCE.getMetaEntityFloat(registerMeta.index, NumberConversions.toFloat(1)))
            }
        } else {
            visible(viewer, true)
        }
    }
}