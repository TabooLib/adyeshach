package ink.ptms.adyeshach.module.editor.action

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.module.editor.lang
import ink.ptms.adyeshach.module.editor.page.Page
import org.bukkit.entity.Player
import taboolib.module.chat.colored
import java.util.*

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.action.SimpleAction
 *
 * @author 坏黑
 * @since 2022/12/27 01:36
 */
abstract class SimpleAction(val id: String) : Action {

    override fun id(): String {
        return id
    }

    open class Literal(val display: String, val description: String? = null) : SimpleAction(UUID.randomUUID().toString()) {

        override fun display(player: Player): String {
            return display.colored()
        }

        override fun description(player: Player): String? {
            return description?.colored()
        }

        override fun isCustomCommand(): Boolean {
            return true
        }
    }

    class Meta(val node: String, val editor: String, val hasDescription: Boolean = false, private val isResettable: Boolean = true) : SimpleAction(node) {

        override fun display(player: Player): String {
            return player.lang("meta-$node")
        }

        override fun description(player: Player): String? {
            return if (hasDescription) player.lang("meta-$node-description") else null
        }

        override fun isResettable(): Boolean {
            return isResettable
        }

        override fun isCustomCommand(): Boolean {
            return true
        }

        override fun clickCommand(player: Player, entity: EntityInstance, page: Page, index: Int): String {
            return "adyeshach edit ${entity.uniqueId} e:${id()}->$editor" // 使用 EDIT 并选用特定编辑器
        }

        override fun isRefreshPage(): Boolean {
            return false
        }
    }

    class MetaBool(val node: String, val value: Boolean, val hasDescription: Boolean = false, private val isResettable: Boolean = true) : SimpleAction(node) {

        override fun display(player: Player): String {
            val text = player.lang("meta-$node")
            return if (value) "&a&n$text".colored() else "&7$text".colored()
        }

        override fun description(player: Player): String? {
            return if (hasDescription) player.lang("meta-$node-description") else null
        }

        override fun isResettable(): Boolean {
            return isResettable
        }

        override fun isCustomCommand(): Boolean {
            return true
        }

        override fun clickCommand(player: Player, entity: EntityInstance, page: Page, index: Int): String {
            return "adyeshach edit ${entity.uniqueId} m:${id().replace("-", "_")}->${!value}" // 使用 MODIFY 切换布尔值
        }
    }
}