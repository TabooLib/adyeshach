package ink.ptms.adyeshach.module.command.subcommand

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.util.asLang
import ink.ptms.adyeshach.core.util.asLangList
import ink.ptms.adyeshach.core.util.safeDistance
import ink.ptms.adyeshach.module.command.toEgg
import ink.ptms.adyeshach.module.editor.format
import ink.ptms.adyeshach.module.editor.lang
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import taboolib.common.platform.command.subCommand
import taboolib.library.xseries.XMaterial
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import taboolib.module.nms.inputSign
import taboolib.module.ui.ClickType
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.Slots
import taboolib.platform.util.buildItem

/**
 * npc list
 */
val listSubCommand = subCommand {
    execute<Player> { sender, _, _ ->
        sender.openListMenu()
    }
}

@Suppress("DuplicatedCode")
private fun Player.openListMenu(search: String? = null) {
    val entities = Adyeshach.api().getEntityFinder().getEntities(this) { it.world == world && !it.isDerived() && (search == null || it.match(search)) }
    // 标题
    val searchTitle = if (search == null) asLang("command-list-title") else asLang("command-list-title-with-search", search)
    // 打开页面
    openMenu<Linked<EntityInstance>>(searchTitle.toString()) {
        rows(6)
        slots(Slots.CENTER)
        elements { entities.sortedBy { it.getLocation().safeDistance(location) } }
        onGenerate { _, element, _, _ ->
            // 构建物品
            buildItem(element.entityType.toEgg()) {
                name = asLang("command-list-item-name", element.id, element.getLocation().safeDistance(location).format())
                lore += asLangList("command-list-item-description", type.name, element.getDisplayName())
            }
        }
        onClick { e, element ->
            if (e.clickType == ClickType.CLICK) {
                // 传送
                if (e.clickEvent().isLeftClick) {
                    closeInventory()
                    performCommand("adyeshach tp ${element.uniqueId}")
                }
                // 编辑
                else if (e.clickEvent().isRightClick) {
                    closeInventory()
                    performCommand("adyeshach edit ${element.uniqueId}")
                }
            }
        }
        set(Slots.LINE_6_MIDDLE, buildItem(XMaterial.OAK_SIGN) {
            name = asLang("command-list-search-name").toString()
            lore += asLang("command-list-search-description").toString()
        }) {
            inputSign(arrayOf(search ?: "", "", "", asLang("command-undo-list-search-placeholder").toString())) {
                openListMenu((it[0] + it[1] + it[2]).ifBlank { null })
            }
        }
        setNextPage(Slots.LINE_6_MIDDLE + 2) { _, _ ->
            buildItem(XMaterial.ARROW) {
                name = "&7${lang("next")}"
                colored()
            }
        }
        setPreviousPage(Slots.LINE_6_MIDDLE - 2) { _, _ ->
            buildItem(XMaterial.ARROW) {
                name = "&7${lang("previous")}"
                colored()
            }
        }
    }
}

private fun EntityInstance.match(search: String): Boolean {
    val conf = Configuration.loadFromString(toJson(), Type.JSON)
    return conf.getValues(true).any { it.value.toString().contains(search, true) }
}