package ink.ptms.adyeshach.common.editor

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityMetaable
import ink.ptms.adyeshach.common.entity.type.AdyEntityLiving
import net.md_5.bungee.api.ChatColor
import org.bukkit.Color
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import org.bukkit.util.NumberConversions
import taboolib.library.xseries.XMaterial
import taboolib.module.nms.getName
import taboolib.module.nms.inputSign
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.module.ui.type.Linked
import taboolib.platform.util.buildItem
import taboolib.platform.util.dispatchCommand
import taboolib.platform.util.inventoryCenterSlots
import kotlin.reflect.KClass

object Editors {

    private val cacheEnums = HashMap<String, Array<out Any>>()
    private val cacheEquipment = HashMap<EquipmentSlot, EntityMetaable.MetaEditor>()

    fun getEnums(enum: KClass<*>): Array<out Any> {
        return cacheEnums.computeIfAbsent(enum.java.name) { enum.java.enumConstants }
    }

    val TEXT = EntityMetaable.MetaEditor()
        .modify { player, entity, meta ->
            player.inputSign(arrayOf("${entity.getMetadata<Any>(meta.key)}", "", "请在第一行输入内容")) {
                if (it[0].isNotEmpty()) {
                    entity.setMetadata(meta.key, "${it[0]}${it[1]}".replace("&", "§"))
                }
                Editor.open(player, entity)
            }
        }
        .display { _, entity, meta ->
            val display = entity.getMetadata<Any>(meta.key).toString()
            if (display.isEmpty()) "§7_" else Editor.toSimple(display)
        }

    val COLOR = EntityMetaable.MetaEditor()
        .modify { player, entity, meta ->
            player.inputSign(arrayOf(meta.editor!!.onDisplay!!.invoke(player, entity, meta), "", "请在第一行输入内容")) {
                if (it[0].isNotEmpty()) {
                    val v = it[0].split("-").map { a -> NumberConversions.toInt(a) }
                    entity.setMetadata(meta.key, Color.fromRGB(v.getOrElse(0) { 0 }, v.getOrElse(1) { 0 }, v.getOrElse(2) { 0 }).asRGB())
                }
                Editor.open(player, entity)
            }
        }
        .display { _, entity, meta ->
            val color = Color.fromRGB(entity.getMetadata(meta.key))
            if (Editor.version >= 11600) {
                "${ChatColor.of(java.awt.Color(color.red, color.green, color.blue))}${color.red}-${color.green}-${color.blue}"
            } else {
                "${color.red}-${color.green}-${color.blue}"
            }
        }

    val ITEM = EntityMetaable.MetaEditor()
        .modify { player, entity, meta ->
            player.openMenu<Basic>("Adyeshach Editor : Input") {
                rows(1)
                map("####@####")
                set('#', XMaterial.BLACK_STAINED_GLASS_PANE) {
                    name = "§f"
                }
                set('@', meta.def as ItemStack)
                onClick('#')
                onClose {
                    entity.setMetadata(meta.key, it.inventory.getItem(4) ?: ItemStack(Material.AIR))
                    Editor.open(player, entity)
                }
            }
        }
        .display { player, entity, meta ->
            entity.getMetadata<ItemStack>(meta.key).getName(player)
        }

    val MATERIAL_DATA = EntityMetaable.MetaEditor()
        .modify { player, entity, meta ->
            player.openMenu<Basic>("Adyeshach Editor : Input") {
                rows(1)
                map("####@####")
                set('#', XMaterial.BLACK_STAINED_GLASS_PANE) {
                    name = "§f"
                }
                set('@', (meta.def as MaterialData).toItemStack(1))
                onClick('#')
                onClose {
                    try {
                        entity.setMetadata(meta.key, (it.inventory.getItem(4) ?: ItemStack(Material.AIR)).data!!)
                    } catch (t: Throwable) {
                        t.printStackTrace()
                    }
                    Editor.open(player, entity)
                }
            }
        }
        .display { player, entity, meta ->
            entity.getMetadata<MaterialData>(meta.key).toItemStack(1).getName(player)
        }

    fun enums(enum: KClass<*>, command: (Player, EntityInstance, EntityMetaable.Meta, Int, Any) -> (String)): EntityMetaable.MetaEditor {
        return EntityMetaable.MetaEditor()
            .modify { player, entity, meta ->
                val enums = getEnums(enum)
                player.openMenu<Linked<Any>>("${enum.simpleName} [Pg. %p]") {
                    rows(6)
                    slots(inventoryCenterSlots)
                    elements {
                        enums.toList()
                    }
                    onClick { e, obj ->
                        val str = command(player, entity, meta, inventoryCenterSlots.indexOf(e.rawSlot) + (enums.size * page), obj)
                        dispatchCommand(player, str.substring(1))
                    }
                    onGenerate { _, element, _, _ ->
                        if (element is DyeColor && Editor.version >= 11600) {
                            val dye = ChatColor.of(java.awt.Color(element.color.red, element.color.green, element.color.blue))
                            buildItem(XMaterial.PAPER) {
                                name = "&7${dye}${Editor.toSimple(element.toString())}"
                                lore += "&8CLICK TO SELECT"
                                colored()
                            }
                        } else {
                            buildItem(XMaterial.PAPER) {
                                name = "&7${Editor.toSimple(element.toString())}"
                                lore += "&8CLICK TO SELECT"
                                colored()
                            }
                        }
                    }
                }
            }.toEnum(enum)
    }

    fun equip(equipmentSlot: EquipmentSlot): EntityMetaable.MetaEditor {
        return cacheEquipment.computeIfAbsent(equipmentSlot) {
            EntityMetaable.MetaEditor()
                .reset { entity, _ ->
                    (entity as AdyEntityLiving).setEquipment(equipmentSlot, ItemStack(Material.AIR))
                }
                .modify { player, entity, _ ->
                    player.openMenu<Basic>("Adyeshach Editor : Input") {
                        rows(1)
                        map("####@####")
                        set('#', XMaterial.BLACK_STAINED_GLASS_PANE) {
                            name = "§f"
                        }
                        set('@', (entity as AdyEntityLiving).getEquipment(equipmentSlot) ?: ItemStack(Material.AIR))
                        onClick('#')
                        onClose {
                            entity.setEquipment(equipmentSlot, it.inventory.getItem(4) ?: ItemStack(Material.AIR))
                            Editor.open(player, entity)
                        }
                    }
                }
                .display { player, entity, _ ->
                    try {
                        ((entity as AdyEntityLiving).getEquipment(equipmentSlot) ?: ItemStack(Material.AIR)).getName(player)
                    } catch (ignored: NullPointerException) {
                        "NULL"
                    }
                }
        }
    }
}