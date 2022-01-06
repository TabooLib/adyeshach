@file:Suppress("UNCHECKED_CAST")

package ink.ptms.adyeshach.common.entity.editor

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.Meta
import ink.ptms.adyeshach.common.entity.MetaMasked
import ink.ptms.adyeshach.common.entity.type.AdyEntityLiving
import net.md_5.bungee.api.ChatColor
import org.bukkit.Color
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.adaptPlayer
import taboolib.common5.Coerce
import taboolib.library.xseries.XMaterial
import taboolib.module.chat.colored
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.getName
import taboolib.module.nms.inputSign
import taboolib.module.ui.ClickType
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.module.ui.type.Linked
import taboolib.platform.util.*
import taboolib.type.BukkitEquipment
import java.util.concurrent.ConcurrentHashMap

internal val minecraftVersion = MinecraftVersion.majorLegacy
internal val cacheEnums = ConcurrentHashMap<String, Array<out Any>>()
internal val allowSlots = listOf(
    EquipmentSlot(13, BukkitEquipment.HEAD, XMaterial.LEATHER_HELMET),
    EquipmentSlot(22, BukkitEquipment.CHEST, XMaterial.LEATHER_CHESTPLATE),
    EquipmentSlot(31, BukkitEquipment.LEGS, XMaterial.LEATHER_LEGGINGS),
    EquipmentSlot(40, BukkitEquipment.FEET, XMaterial.LEATHER_BOOTS),
    EquipmentSlot(21, BukkitEquipment.HAND, XMaterial.WOODEN_SWORD),
    EquipmentSlot(23, BukkitEquipment.OFF_HAND, XMaterial.SHIELD)
)

internal class EquipmentSlot(val slot: Int, val equipment: BukkitEquipment, val material: XMaterial)

/**
 * 获取所有枚举
 */
internal fun Class<*>.enums(): Array<out Any> {
    return cacheEnums.computeIfAbsent(name) { enumConstants }
}

/**
 * 根据版本返回内容
 */
internal fun <T> at(vararg index: Pair<Int, T>): T {
    return (index.firstOrNull { minecraftVersion >= it.first }?.second ?: -1) as T
}

/**
 * 遍历所有允许修改的元数据
 */
internal fun EntityInstance.forEachMeta(func: (Meta<*>, Boolean) -> Unit) {
    getEditableEntityMeta().forEach { func.invoke(it, false) }
}

/**
 * 遍历所有允许修改的元数据
 * 经过类型排序
 */
internal fun EntityInstance.forEachMetaSorted(player: Player, func: (Meta<*>, Boolean) -> Unit) {
    getEditableEntityMeta().mapNotNull {
        try {
            it to when {
                UnusedMetas.isUnusedMeta(this, it) -> 0
                it.isBooleanType(player, this) -> 1
                else -> 2
            }
        } catch (ex: Throwable) {
            if (ex.message?.contains("not supported") != true) {
                ex.printStackTrace()
            }
            null
        }
    }.sortedByDescending { it.second }.forEach { func.invoke(it.first, UnusedMetas.isUnusedMeta(this, it.first)) }
}

/**
 * 是否为布尔值类型的元数据
 */
internal fun Meta<*>.isBooleanType(player: Player, entity: EntityInstance): Boolean {
    val display = (editor as MetaEditor<EntityInstance>).displayGenerator?.invoke(player, entity) ?: entity.getMetadata(key)
    return this is MetaMasked<*> || display == player.asLangText("editor-meta-true") || display == player.asLangText("editor-meta-false")
}

/**
 * 压缩文本
 */
internal fun String.minimize(): String {
    return if (length > 16) substring(0, length - (length - 10)) + "..." + substring(length - 7) else this
}

/**
 * 获取展示类型
 */
internal fun Boolean?.toDisplay(player: Player): String {
    return if (this == true) player.asLangText("editor-meta-true") else player.asLangText("editor-meta-false")
}

/**
 * 获取展示类型
 */
internal fun String?.toDisplay(): String {
    val builder = StringBuilder()
    toString().toCharArray().forEachIndexed { index, c ->
        when {
            index == 0 -> builder.append(c.uppercaseChar())
            c.isUpperCase() -> builder.append(" $c")
            else -> builder.append(c)
        }
    }
    return builder.toString()
}

/**
 * 获取语言文件节点
 */
internal fun String.toLocale(player: Player): String {
    return player.asLangText("editor-meta-${toLocaleKey()}")
}

/**
 * 获取语言文件节点
 */
internal fun String.toLocaleKey(): String {
    val builder = StringBuilder()
    toString().toCharArray().forEachIndexed { _, c ->
        when {
            c.isUpperCase() -> builder.append("-${c.lowercaseChar()}")
            else -> builder.append(c)
        }
    }
    return builder.toString()
}

/**
 * 获取字符串的实际长度
 */
internal fun String.realLength(): Int {
    val regex = "[\u3091-\uFFe5]".toRegex()
    return sumBy { if (it.toString().matches(regex)) 2 else 1 }
}

/**
 * 使玩家打开告示牌编辑单位
 */
internal fun Player.edit(entity: EntityInstance, value: Any, function: (value: String) -> Unit) {
    inputSign(arrayOf("$value", "", "§f${asLangText("editor-sign-input")}")) { args ->
        if (args[0].isNotEmpty()) {
            function((args[0] + args[1]).colored())
        }
        entity.openEditor(this)
    }
}

internal fun MetaEditor<*>.useColorEditor() {
    modify { player, entity ->
        player.edit(entity, (meta.editor as MetaEditor<EntityInstance>).displayGenerator!!(player, entity)) {
            val args = it.split("-").map { a -> Coerce.toInteger(a) }
            entity.setMetadata(meta.key, Color.fromRGB(args.getOrElse(0) { 0 }, args.getOrElse(1) { 0 }, args.getOrElse(2) { 0 }).asRGB())
        }
    }
    display { _, entity ->
        val color = Color.fromRGB(entity.getMetadata<Int>(meta.key).let { if (it == -1) 0 else it })
        if (minecraftVersion >= 11600) {
            "${ChatColor.of(java.awt.Color(color.red, color.green, color.blue))}${color.red}-${color.green}-${color.blue}"
        } else {
            "${color.red}-${color.green}-${color.blue}"
        }
    }
}

internal fun <T : EntityInstance> MetaEditor<T>.useIndexEditor(type: Class<*>, key: String) {
    useEnumsEditor(type = type, key = "int", useIndex = true) { type.enums()[getMetadata(key)] }
}

internal fun <T : EntityInstance> MetaEditor<T>.useEnumsEditor(
    type: Class<*>? = null,
    key: String = meta.key,
    useIndex: Boolean = false,
    display: T.() -> Any = { getMetadata(key) },
) {
    enumType = type
    modify { player, entity ->
        val enums = (type ?: meta.def.javaClass).enums()
        player.openMenu<Linked<Any>>("${(type ?: meta.def.javaClass).simpleName} [Pg. %p]") {
            rows(6)
            slots(inventoryCenterSlots)
            elements {
                enums.toList()
            }
            onClick { _, element ->
                if (useIndex) {
                    adaptPlayer(player).performCommand("adyeshachapi edit $key ${entity.uniqueId} ${meta.key} ${enums.indexOf(element)}")
                } else {
                    adaptPlayer(player).performCommand("adyeshachapi edit $key ${entity.uniqueId} ${meta.key} $element")
                }
            }
            onGenerate { _, element, _, _ ->
                if (element is DyeColor && minecraftVersion >= 11600) {
                    val dye = ChatColor.of(java.awt.Color(element.color.red, element.color.green, element.color.blue))
                    buildItem(XMaterial.PAPER) {
                        name = "&7${dye}${element.toString().minimize()}"
                        lore += player.asLangText("editor-select")
                        colored()
                    }
                } else {
                    buildItem(XMaterial.PAPER) {
                        name = "&7${element.toString().minimize()}"
                        lore += player.asLangText("editor-select")
                        colored()
                    }
                }
            }
        }
    }
    display { _, entity -> display(entity) }
}

internal fun MetaEditor<AdyEntityLiving>.useEquipmentEditor(equipmentSlot: EquipmentSlot) {
    reset { _, entity ->
        entity.setEquipment(equipmentSlot, ItemStack(Material.AIR))
    }
    modify { player, entity ->
        player.openMenu<Basic>(player.asLangText("editor-item-input")) {
            handLocked(false)
            rows(1)
            map("####@####")
            set('#', XMaterial.BLACK_STAINED_GLASS_PANE) { name = "§f" }
            set('@', entity.getEquipment(equipmentSlot) ?: ItemStack(Material.AIR))
            onClick('#')
            onClose {
                entity.setEquipment(equipmentSlot, it.inventory.getItem(4) ?: ItemStack(Material.AIR))
                entity.openEditor(player)
            }
        }
    }
    display { player, entity ->
        try {
            (entity.getEquipment(equipmentSlot) ?: ItemStack(Material.AIR)).getName(player)
        } catch (ignored: Exception) {
            "-"
        }
    }
}

internal fun MetaEditor<AdyEntityLiving>.useHybridEquipmentEditor() {
    hybrid = true
    display { player, _ -> player.asLangText("editor-no-preview") }
    modify { player, entity ->
        player.openMenu<Basic>(player.asLangText("editor-item-input")) {
            handLocked(false)
            rows(6)
            onBuild { _, inventory ->
                allowSlots.forEach { eq ->
                    val equipment = entity.getEquipment(eq.equipment.bukkit)
                    if (equipment.isAir()) {
                        inventory.setItem(eq.slot, buildItem(eq.material) {
                            name = "§7${player.asLangText("editor-meta-equipment-empty")}"
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
                                        name = "§7${player.asLangText("editor-meta-equipment-empty")}"
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