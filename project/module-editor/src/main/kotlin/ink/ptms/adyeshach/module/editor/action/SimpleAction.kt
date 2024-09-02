package ink.ptms.adyeshach.module.editor.action

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.module.editor.EditType
import ink.ptms.adyeshach.module.editor.lang
import ink.ptms.adyeshach.module.editor.page.Page
import ink.ptms.adyeshach.module.editor.toLocaleKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
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

    object None : Literal("none")

    open class Literal(
        private val display: String = "",
        private val description: String? = null
    ) : SimpleAction(UUID.randomUUID().toString()) {

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

    open class Meta(
        val node: String,
        val editor: EditType,
        val value: Any? = null,
        val hasDescription: Boolean = false,
        private val isResettable: Boolean = true
    ) : SimpleAction(node) {

        override fun display(player: Player): String {
            return "&7${player.lang("meta-${node.toLocaleKey()}")}".colored()
        }

        override fun description(player: Player): String? {
            return if (hasDescription) player.lang("meta-${node.toLocaleKey()}-description") else value?.let { "&7$it".colored() }
        }

        override fun isResettable(): Boolean {
            return isResettable
        }

        override fun isCustomCommand(): Boolean {
            return true
        }

        override fun clickCommand(player: Player, entity: EntityInstance, page: Page, index: Int): String {
            // 使用 EDIT 并选用特定编辑器
            // 物品类型不显示值
            return if (value is ItemStack) {
                "adyeshach edit ${entity.uniqueId} e:${id()}->$editor"
            } else {
                "adyeshach edit ${entity.uniqueId} e:${id()}->$editor ${value ?: ""}"
            }
        }

        override fun isRefreshPage(): Boolean {
            return false
        }
    }

    open class MetaBool(
        val node: String,
        val value: Boolean,
        val hasDescription: Boolean = false,
        private val isResettable: Boolean = true
    ) : SimpleAction(node) {

        override fun display(player: Player): String {
            val text = player.lang("meta-${node.toLocaleKey()}")
            return if (value) "&a&n$text".colored() else "&6$text".colored()
        }

        override fun description(player: Player): String? {
            return if (hasDescription) player.lang("meta-${node.toLocaleKey()}-description") else null
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

    class MetaTrait(node: String, value: Any) : Meta(node, EditType.AUTO, value, false, false) {

        override fun clickCommand(player: Player, entity: EntityInstance, page: Page, index: Int): String {
            return "adyeshach edit ${entity.uniqueId} t:$node" // 使用 TRAIT 编辑特性
        }
    }

    class MetaTraitBool(node: String, value: Boolean) : MetaBool(node, value, false, false) {

        override fun clickCommand(player: Player, entity: EntityInstance, page: Page, index: Int): String {
            return "adyeshach edit ${entity.uniqueId} t:$node" // 使用 TRAIT 编辑特性
        }
    }

    class MetaTraitLiteral(node: String) : Meta(node, EditType.AUTO, null, true, false) {

        override fun clickCommand(player: Player, entity: EntityInstance, page: Page, index: Int): String {
            return "adyeshach edit ${entity.uniqueId} t:$node" // 使用 TRAIT 编辑特性
        }
    }
}