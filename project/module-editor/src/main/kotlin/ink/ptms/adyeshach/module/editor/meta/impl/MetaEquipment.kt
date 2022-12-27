package ink.ptms.adyeshach.module.editor.meta.impl

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.type.AdyEntityLiving
import ink.ptms.adyeshach.module.editor.lang
import ink.ptms.adyeshach.module.editor.meta.MetaEditor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.ClickType
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.buildItem
import taboolib.platform.util.isAir
import taboolib.platform.util.isNotAir
import taboolib.type.BukkitEquipment

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.meta.impl.MetaEquipment
 *
 * @author 坏黑
 * @since 2022/12/27 04:05
 */
class MetaEquipment : MetaEditor {

    class EquipmentSlot(val slot: Int, val equipment: BukkitEquipment, val material: XMaterial)

    val allowSlots = listOf(
        EquipmentSlot(13, BukkitEquipment.HEAD, XMaterial.LEATHER_HELMET),
        EquipmentSlot(22, BukkitEquipment.CHEST, XMaterial.LEATHER_CHESTPLATE),
        EquipmentSlot(31, BukkitEquipment.LEGS, XMaterial.LEATHER_LEGGINGS),
        EquipmentSlot(40, BukkitEquipment.FEET, XMaterial.LEATHER_BOOTS),
        EquipmentSlot(21, BukkitEquipment.HAND, XMaterial.WOODEN_SWORD),
        EquipmentSlot(23, BukkitEquipment.OFF_HAND, XMaterial.SHIELD)
    )

    override fun open(entity: EntityInstance, player: Player, def: String) {
        entity as AdyEntityLiving
        player.openMenu<Basic>(player.lang("input-item")) {
            handLocked(false)
            rows(6)
            onBuild { _, inventory ->
                allowSlots.forEach { eq ->
                    val equipment = entity.getEquipment(eq.equipment.bukkit)
                    if (equipment.isAir()) {
                        inventory.setItem(eq.slot, buildItem(eq.material) {
                            name = "§7${player.lang("meta-equipment-empty")}"
                        })
                    } else {
                        inventory.setItem(eq.slot, equipment)
                    }
                }
            }
            onClick { e ->
                if (e.clickType == ClickType.DRAG && e.dragEvent().rawSlots.size > 1) {
                    e.isCancelled = true
                } else {
                    val rawSlot = if (e.clickType == ClickType.DRAG) e.dragEvent().rawSlots.firstOrNull() ?: -1 else e.rawSlot
                    // 点击箱子内部
                    if (rawSlot in 0..54) {
                        e.isCancelled = true
                        // 点击装备格子
                        val slot = allowSlots.firstOrNull { it.slot == rawSlot }
                        if (slot != null) {
                            val currentItem = entity.getEquipment(slot.equipment.bukkit)
                            val cursor = player.itemOnCursor
                            when {
                                // 放入
                                currentItem.isAir() && cursor.isNotAir() -> {
                                    player.setItemOnCursor(null)
                                    entity.setEquipment(slot.equipment.bukkit, cursor)
                                    e.inventory.setItem(rawSlot, cursor)
                                }
                                // 取出
                                currentItem.isNotAir() && cursor.isAir() -> {
                                    player.setItemOnCursor(currentItem)
                                    entity.setEquipment(slot.equipment.bukkit, ItemStack(Material.AIR))
                                    e.currentItem = buildItem(slot.material) {
                                        name = "§7${player.lang("meta-equipment-empty")}"
                                    }
                                }
                                // 交换
                                currentItem.isNotAir() && currentItem.isNotAir() -> {
                                    player.setItemOnCursor(currentItem)
                                    entity.setEquipment(slot.equipment.bukkit, cursor)
                                    e.inventory.setItem(rawSlot, cursor)
                                }
                            }
                        }
                    } else if (e.clickType == ClickType.CLICK && e.clickEvent().isShiftClick) {
                        e.isCancelled = true
                    }
                }
            }
        }
    }
}