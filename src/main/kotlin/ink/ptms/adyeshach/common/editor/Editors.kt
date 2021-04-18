package ink.ptms.adyeshach.common.editor

import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityMetaable
import ink.ptms.adyeshach.common.entity.type.AdyEntityLiving
import io.izzel.taboolib.internal.xseries.XMaterial
import io.izzel.taboolib.module.i18n.I18n
import io.izzel.taboolib.util.Features
import io.izzel.taboolib.util.item.ItemBuilder
import io.izzel.taboolib.util.item.Items
import io.izzel.taboolib.util.item.inventory.ClickEvent
import io.izzel.taboolib.util.item.inventory.MenuBuilder
import io.izzel.taboolib.util.item.inventory.linked.MenuLinked
import io.izzel.taboolib.util.lite.Materials
import io.izzel.taboolib.util.lite.Signs
import org.bukkit.Color
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import org.bukkit.util.NumberConversions
import kotlin.reflect.KClass

object Editors {

    private val cacheEnums = HashMap<String, Array<out Any>>()
    private val cacheEquipment = HashMap<EquipmentSlot, EntityMetaable.MetaEditor>()

    fun getEnums(enum: KClass<*>): Array<out Any> {
        return cacheEnums.computeIfAbsent(enum.java.name) { enum.java.enumConstants }
    }

    val TEXT = EntityMetaable.MetaEditor()
            .modify { player, entity, meta ->
                Signs.fakeSign(player, arrayOf("${entity.getMetadata<Any>(meta.key)}", "", "请在第一行输入内容")) {
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
                Signs.fakeSign(player, arrayOf(meta.editor!!.onDisplay!!.invoke(player, entity, meta), "", "请在第一行输入内容")) {
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
                    "${io.izzel.taboolib.util.chat.ChatColor.of(java.awt.Color(color.red, color.green, color.blue))}${color.red}-${color.green}-${color.blue}"
                } else {
                    "${color.red}-${color.green}-${color.blue}"
                }
            }

    val ITEM = EntityMetaable.MetaEditor()
            .modify { player, entity, meta ->
                MenuBuilder.builder(Adyeshach.plugin)
                        .title("Adyeshach Editor : Input")
                        .rows(1)
                        .items("####@####")
                        .put('#', ItemBuilder(Materials.BLACK_STAINED_GLASS_PANE.parseItem()).name("§f").build())
                        .put('@', meta.def as ItemStack)
                        .event {
                            if (it.slot == '#') {
                                it.isCancelled = true
                            }
                        }.close {
                            entity.setMetadata(meta.key, it.inventory.getItem(4) ?: ItemStack(Material.AIR))
                            Editor.open(player, entity)
                        }.open(player)
            }
            .display { player, entity, meta ->
                I18n.get().getName(player, entity.getMetadata<ItemStack>(meta.key))
            }

    val MATERIAL_DATA = EntityMetaable.MetaEditor()
            .modify { player, entity, meta ->
                MenuBuilder.builder(Adyeshach.plugin)
                        .title("Adyeshach Editor : Input")
                        .rows(1)
                        .items("####@####")
                        .put('#', ItemBuilder(Materials.BLACK_STAINED_GLASS_PANE.parseItem()).name("§f").build())
                        .put('@', (meta.def as MaterialData).toItemStack(1))
                        .event {
                            if (it.slot == '#') {
                                it.isCancelled = true
                            }
                        }.close {
                            try {
                                entity.setMetadata(meta.key, (it.inventory.getItem(4) ?: ItemStack(Material.AIR)).data!!)
                            } catch (t: Throwable) {
                                t.printStackTrace()
                            }
                            Editor.open(player, entity)
                        }.open(player)
            }
            .display { player, entity, meta ->
                I18n.get().getName(player, entity.getMetadata<MaterialData>(meta.key).toItemStack(1))
            }

    fun enums(enum: KClass<*>, command: (Player, EntityInstance, EntityMetaable.Meta, Int, Any) -> (String)): EntityMetaable.MetaEditor {
        return EntityMetaable.MetaEditor()
                .modify { player, entity, meta ->
                    val enums = getEnums(enum)

                    object : MenuLinked<Any>(player) {

                        init {
                            addButtonPreviousPage(47)
                            addButtonNextPage(51)
                        }

                        override fun getTitle(): String {
                            return "${enum.simpleName} [Pg. ${page + 1}]"
                        }

                        override fun getRows(): Int {
                            return 6
                        }

                        override fun getElements(): MutableList<Any> {
                            return enums.toMutableList()
                        }

                        override fun getSlots(): MutableList<Int> {
                            return Items.INVENTORY_CENTER.toMutableList()
                        }

                        override fun onBuild(inventory: Inventory) {
                            if (hasPreviousPage()) {
                                inventory.setItem(47, ItemBuilder(XMaterial.SPECTRAL_ARROW).name("§fPrevious").build())
                            } else {
                                inventory.setItem(47, ItemBuilder(XMaterial.ARROW).name("§8Previous").build())
                            }
                            if (hasNextPage()) {
                                inventory.setItem(51, ItemBuilder(XMaterial.SPECTRAL_ARROW).name("§fNext").build())
                            } else {
                                inventory.setItem(51, ItemBuilder(XMaterial.ARROW).name("§8Next").build())
                            }
                        }

                        override fun onClick(e: ClickEvent, obj: Any) {
                            val cmd = command(player, entity, meta, Items.INVENTORY_CENTER.indexOf(e.rawSlot) + (Items.INVENTORY_CENTER.size * page), obj)
                            Features.dispatchCommand(player, cmd.substring(1))
                        }

                        override fun generateItem(player: Player, obj: Any, index: Int, slot: Int): ItemStack {
                            return if (obj is DyeColor && Editor.version >= 11600) {
                                ItemBuilder(XMaterial.PAPER)
                                    .name("&7${io.izzel.taboolib.util.chat.ChatColor.of(java.awt.Color(obj.color.red, obj.color.green, obj.color.blue))}${Editor.toSimple(obj.toString())}")
                                    .lore("&8CLICK TO SELECT")
                                    .colored()
                                    .build()
                            } else {
                                ItemBuilder(XMaterial.PAPER)
                                    .name("&7${Editor.toSimple(obj.toString())}")
                                    .lore("&8CLICK TO SELECT")
                                    .colored()
                                    .build()
                            }
                        }
                    }.open()
                }.toEnum(enum)
    }

    fun equip(equipmentSlot: EquipmentSlot): EntityMetaable.MetaEditor {
        return cacheEquipment.computeIfAbsent(equipmentSlot) {
            EntityMetaable.MetaEditor()
                    .reset { entity, _ ->
                        (entity as AdyEntityLiving).setEquipment(equipmentSlot, ItemStack(Material.AIR))
                    }
                    .modify { player, entity, _ ->
                        MenuBuilder.builder(Adyeshach.plugin)
                                .title("Adyeshach Editor : Input")
                                .rows(1)
                                .items("####@####")
                                .put('#', ItemBuilder(Materials.BLACK_STAINED_GLASS_PANE.parseItem()).name("§f").build())
                                .put('@', (entity as AdyEntityLiving).getEquipment(equipmentSlot) ?: ItemStack(Material.AIR))
                                .event {
                                    if (it.slot == '#') {
                                        it.isCancelled = true
                                    }
                                }.close {
                                    entity.setEquipment(equipmentSlot, it.inventory.getItem(4) ?: ItemStack(Material.AIR))
                                    Editor.open(player, entity)
                                }.open(player)
                    }
                    .display { player, entity, _ ->
                        try {
                            I18n.get().getName(player, (entity as AdyEntityLiving).getEquipment(equipmentSlot) ?: ItemStack(Material.AIR))
                        } catch (ignored: NullPointerException) {
                            "NULL"
                        }
                    }
        }
    }
}