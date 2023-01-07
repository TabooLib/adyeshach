package ink.ptms.adyeshach.module.command.subcommand

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.manager.ManagerType
import ink.ptms.adyeshach.core.util.asLang
import ink.ptms.adyeshach.core.util.asLangList
import ink.ptms.adyeshach.core.util.getEnum
import ink.ptms.adyeshach.core.util.sendLang
import ink.ptms.adyeshach.module.command.EntityTracker
import ink.ptms.adyeshach.module.command.toEgg
import ink.ptms.adyeshach.module.editor.lang
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggestUncheck
import taboolib.common.platform.function.getDataFolder
import taboolib.library.xseries.XMaterial
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import taboolib.module.nms.inputSign
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.Slots
import taboolib.platform.util.buildItem
import java.io.File

private const val STANDARD_REMOVE_TRACKER = "remove"

/**
 * npc undo (uuid)
 */
val undoSubCommand = subCommand {
    dynamic("uuid") {
        suggestUncheck { File(getDataFolder(), "npc/trash").listFiles()?.filter { it.extension == "json" }?.map { it.nameWithoutExtension } }
        // 定向重命名
        execute<CommandSender> { sender, _, args ->
            val file = File(getDataFolder(), "npc/trash/$args.json")
            if (file.exists()) {
                try {
                    // 恢复单位
                    val entity = Adyeshach.api().getPublicEntityManager(ManagerType.PERSISTENT).loadEntityFromFile(file)
                    // 刷新追踪器
                    val tracker = EntityTracker.get(sender, STANDARD_REMOVE_TRACKER)
                    if (tracker != null) {
                        val elements = tracker.entitySource.elements
                        val index = elements.indexOfFirst { it.uniqueId == entity.uniqueId }
                        if (index > -1) {
                            elements[index] = entity
                            tracker.print()
                        }
                    }
                    file.delete()
                    sender.sendLang("command-undo-success", entity.id)
                } catch (ex: Throwable) {
                    sender.sendLang("command-undo-failed", args, ex.message.toString())
                }
            } else {
                sender.sendLang("command-undo-not-found")
            }
        }
    }
    execute<Player> { sender, _, _ ->
        sender.openTrashMenu()
    }
}

@Suppress("DuplicatedCode")
private fun Player.openTrashMenu(search: String? = null) {
    // 获取所有文件
    val files = File(getDataFolder(), "npc/trash").listFiles()?.filter { it.extension == "json" } ?: emptyList()
    // 标题
    val searchTitle = if (search == null) asLang("command-undo-list-title") else asLang("command-undo-list-title-with-search", search)
    // 打开页面
    openMenu<Linked<File>>(searchTitle.toString()) {
        rows(6)
        slots(Slots.CENTER)
        elements { files.filter { search == null || it.match(search) }.sortedByDescending { it.lastModified() } }
        onGenerate { _, element, _, _ ->
            val conf = Configuration.loadFromFile(element, Type.JSON)
            // 类型
            val type = EntityTypes::class.java.getEnum(conf["entityType"].toString())
            // 展示名称
            val customName = conf["gameProfile.name"] ?: conf["metadata.customName"] ?: type.name
            // 删除时间
            val time = System.currentTimeMillis() - element.lastModified()
            val timeString = buildString {
                val hour = time / 1000 / 60 / 60
                val minute = time / 1000 / 60 % 60
                val second = time / 1000 % 60
                if (hour > 0) {
                    append(hour).append(asLang("command-undo-list-time-hour"))
                }
                if (minute > 0) {
                    append(minute).append(asLang("command-undo-list-time-minute"))
                }
                if (second > 0) {
                    append(second).append(asLang("command-undo-list-time-second"))
                }
            }
            // 构建物品
            buildItem(type.toEgg()) {
                name = asLang("command-undo-list-item-name", conf["id"].toString())
                lore += asLangList("command-undo-list-item-description", conf["id"].toString(), type.name, customName, timeString)
            }
        }
        onClick { _, element ->
            performCommand("adyeshach undo ${element.nameWithoutExtension}")
            openTrashMenu(search)
        }
        set(Slots.LINE_6_MIDDLE, buildItem(XMaterial.OAK_SIGN) {
            name = asLang("command-undo-list-search-name").toString()
            lore += asLang("command-undo-list-search-description").toString()
        }) {
            inputSign(arrayOf(search ?: "", "", "", asLang("command-undo-list-search-placeholder").toString())) {
                openTrashMenu((it[0] + it[1] + it[2]).ifBlank { null })
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

private fun File.match(search: String): Boolean {
    val conf = Configuration.loadFromFile(this, Type.JSON)
    return conf.getValues(true).any { it.value.toString().contains(search, true) }
}