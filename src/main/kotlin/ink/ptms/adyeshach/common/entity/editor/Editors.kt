package ink.ptms.adyeshach.common.entity.editor

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityMetaable
import ink.ptms.adyeshach.common.entity.editor.MetaEditor
import ink.ptms.adyeshach.common.entity.type.AdyEntityLiving
import net.md_5.bungee.api.ChatColor
import org.bukkit.Color
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.util.NumberConversions
import taboolib.common.platform.function.adaptPlayer
import taboolib.library.xseries.XMaterial
import taboolib.module.nms.getName
import taboolib.module.nms.inputSign
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.module.ui.type.Linked
import taboolib.platform.util.buildItem
import taboolib.platform.util.inventoryCenterSlots
import kotlin.reflect.KClass

object Editors {

//    val COLOR = EntityMetaable.MetaEditor()
//        .modify { player, entity, meta ->
//            player.inputSign(arrayOf(meta.editor!!.onDisplay!!.invoke(player, entity, meta), "", "请在第一行输入内容")) {
//                if (it[0].isNotEmpty()) {
//                    val v = it[0].split("-").map { a -> NumberConversions.toInt(a) }
//                    entity.setMetadata(meta.key, Color.fromRGB(v.getOrElse(0) { 0 }, v.getOrElse(1) { 0 }, v.getOrElse(2) { 0 }).asRGB())
//                }
//                entity.openEditor(player)
//            }
//        }
//        .display { _, entity, meta ->
//            val color = Color.fromRGB(entity.getMetadata(meta.key))
//            if (Editor.version >= 11600) {
//                "${ChatColor.of(java.awt.Color(color.red, color.green, color.blue))}${color.red}-${color.green}-${color.blue}"
//            } else {
//                "${color.red}-${color.green}-${color.blue}"
//            }
//        }
//
//    fun enums(enum: Class<*>, command: (Player, EntityInstance, EntityMetaable.Meta, Int, Any) -> (String)): EntityMetaable.MetaEditor {
//        return EntityMetaable.MetaEditor()
//            .modify { player, entity, meta ->
//                val enums = getEnums(enum)
//                player.openMenu<Linked<Any>>("${enum.simpleName} [Pg. %p]") {
//                    rows(6)
//                    slots(inventoryCenterSlots)
//                    elements {
//                        enums.toList()
//                    }
//                    onClick { e, obj ->
//                        val str = command(player, entity, meta, inventoryCenterSlots.indexOf(e.rawSlot) + (enums.size * page), obj)
//                        adaptPlayer(player).performCommand(str.substring(1))
//                    }
//                    onGenerate { _, element, _, _ ->
//                        if (element is DyeColor && Editor.version >= 11600) {
//                            val dye = ChatColor.of(java.awt.Color(element.color.red, element.color.green, element.color.blue))
//                            buildItem(XMaterial.PAPER) {
//                                name = "&7${dye}${Editor.toSimple(element.toString())}"
//                                lore += "&8CLICK TO SELECT"
//                                colored()
//                            }
//                        } else {
//                            buildItem(XMaterial.PAPER) {
//                                name = "&7${Editor.toSimple(element.toString())}"
//                                lore += "&8CLICK TO SELECT"
//                                colored()
//                            }
//                        }
//                    }
//                }
//            }.toEnum(enum)
//    }
//
//    fun equip(equipmentSlot: EquipmentSlot): EntityMetaable.MetaEditor {
//        return cacheEquipment.computeIfAbsent(equipmentSlot) {
//            EntityMetaable.MetaEditor()
//                .reset { entity, _ ->
//                    (entity as AdyEntityLiving).setEquipment(equipmentSlot, ItemStack(Material.AIR))
//                }
//                .modify { player, entity, _ ->
//                    player.openMenu<Basic>("Adyeshach Editor : Input") {
//                        rows(1)
//                        map("####@####")
//                        set('#', XMaterial.BLACK_STAINED_GLASS_PANE) {
//                            name = "§f"
//                        }
//                        set('@', (entity as AdyEntityLiving).getEquipment(equipmentSlot) ?: ItemStack(Material.AIR))
//                        onClick('#')
//                        onClose {
//                            entity.setEquipment(equipmentSlot, it.inventory.getItem(4) ?: ItemStack(Material.AIR))
//                            entity.openEditor(player)
//                        }
//                    }
//                }
//                .display { player, entity, _ ->
//                    try {
//                        ((entity as AdyEntityLiving).getEquipment(equipmentSlot) ?: ItemStack(Material.AIR)).getName(player)
//                    } catch (ignored: NullPointerException) {
//                        "NULL"
//                    }
//                }
//        }
//    }
}