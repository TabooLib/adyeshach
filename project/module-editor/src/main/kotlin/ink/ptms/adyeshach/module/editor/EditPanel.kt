package ink.ptms.adyeshach.module.editor

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.StandardTags
import ink.ptms.adyeshach.core.entity.TickService
import org.bukkit.entity.Player
import taboolib.module.chat.TellrawJson
import taboolib.module.chat.colored

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.ChatEditor
 *
 * @author 坏黑
 * @since 2022/12/17 14:16
 */
class EditPanel(val player: Player, val entity: EntityInstance) {

    fun open() {
        MainPage(this).open()
    }

    class MainPage(editor: EditPanel) : Page(editor) {

        val derivedId = entity.getTag(StandardTags.DERIVED)

        override fun open() {
            super.open()
            // ID
            appendId().newLine().newLine()
            // 衍生单位
            if (derivedId != null) {
                json.append("  ").appendLang("entity-is-derived", derivedId)
                // 发送半屏消息
                player.sendNativeHalfMessage(json)
            } else {
                json.append("  ")
                // 观察者
                json.appendLang("viewer", entity.viewPlayers.viewers.size).hoverText(hoverViewers())
                // 控制器
                json.append(" ").appendLang("controller", entity.getController().size).hoverText(hoverController())
                // 标签
                json.append(" ").appendLang("tags", entity.getTags().size).hoverText(hoverTags())
                // 关联
                json.append(" ").appendLang("link", linkedCount()).hoverText(hoverLinked()).newLine().newLine()

                // 特性 & 数据 & 微调
                json.append("  ")
                json.appendLang("entity-public-traits").hoverText(player.lang("entity-public-traits-help"))
                json.append(" §8| ")
                json.appendLang("entity-public-meta").hoverText(player.lang("entity-public-meta-help"))
                json.append(" ")
                json.appendLang("entity-private-meta").hoverText(player.lang("entity-private-meta-help"))
                json.append(" §8| ")
                json.appendLang("entity-move").hoverText(player.lang("entity-move-help")).newLine().newLine()

                // 操作
                json.append("  ")
                (0..6).forEach { i ->
                    json.appendLang("entity-tools-$i").hoverText(player.lang("entity-tools-$i-help"))
                    json.append(" ")
                }

                // 展开对话框提示
                json.newLine().newLine().newLine().newLine().newLine()
                json.append("  ").appendLang("open-chat-screen")

                // 发送全屏消息
                player.sendNativeFullMessage(json)
            }
        }

        /** 控制器描述 */
        fun hoverController(): String {
            return entity.getController().map { "&7${it.priority()}. &f${it.id()} &8(${it.key()})" }.colored().joinToString("\n")
        }

        /** 标签描述 */
        fun hoverTags(): String {
            val list = arrayListOf<String>()
            list += entity.getTags().map { "&7${it.key}: &f${it.value}" }
            return list.colored().joinToString("\n")
        }

        /** 关联实体数量 */
        fun linkedCount(): Int {
            return entity.getPassengers().size + if (entity.getVehicle() != null) 1 else 0
        }

        /** 关联实体描述 */
        fun hoverLinked(): String {
            val list = arrayListOf<String>()
            val vehicle = entity.getVehicle()
            if (vehicle != null) {
                list += player.lang("link-vehicle")
                list += "&f - &7${vehicle.id} &8(${vehicle.entityType})"
            }
            val passengers = entity.getPassengers()
            if (passengers.isNotEmpty()) {
                list += player.lang("link-passengers")
                list += passengers.map { "&f - &7${it.id} &8(${it.entityType})" }
            }
            return list.colored().joinToString("\n")
        }

        /** 观察者描述 **/
        fun hoverViewers(): String {
            val list = arrayListOf<String>()
            if (entity.viewPlayers.viewers.isNotEmpty()) {
                list += player.lang("viewer-list-all")
                list += entity.viewPlayers.viewers.map { "&f - &7$it" }
            }
            if (entity.viewPlayers.visible.isNotEmpty()) {
                list += player.lang("viewer-list-visible")
                list += entity.viewPlayers.visible.map { "&f - &7$it" }
            }
            return list.colored().joinToString("\n")
        }

        fun appendId(): TellrawJson {
            json.append("  ")
            json.append("§8§n${entity.uniqueId}").hoverText(player.lang("copy")).suggestCommand(entity.uniqueId)
            json.append(" §7(${entity.id})")
            return json
        }
    }

    abstract class Page(val editor: EditPanel) {

        val player = editor.player
        val entity = editor.entity

        val json = TellrawJson().newLine()

        open fun open() {
            // 清屏
            player.clearScreen()
            // 标题
            appendTitle().newLine()
        }

        fun appendTitle(): TellrawJson {
            json.append("  ")
            // 管理器类型
            val manager = entity.manager
            if (manager?.isPublic() == true) {
                json.appendLang("manager-public")
            } else {
                json.appendLang("manager-private")
            }
            // 孤立单位
            if (manager == null || manager !is TickService) {
                json.appendLang("manager-isolated")
            }
            // 临时单位
            else if (manager.isTemporary()) {
                json.appendLang("manager-temporary")
            }
            // 分页
            json.append(" §8§l${RIGHT_ARROW}§r ")
            // 类型
            json.append("§e${entity.entityType.name}")
            // 分页
            json.append(" §8§l${RIGHT_ARROW}§r ")
            // ID
            json.append("§f${entity.id}")
            return json
        }

        fun TellrawJson.appendLang(node: String, vararg args: Any): TellrawJson {
            return append(player.lang(node, *args))
        }
    }
}