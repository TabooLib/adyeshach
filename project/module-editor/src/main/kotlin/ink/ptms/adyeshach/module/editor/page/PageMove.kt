package ink.ptms.adyeshach.module.editor.page

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.module.editor.EditPanel
import ink.ptms.adyeshach.module.editor.action.Action
import ink.ptms.adyeshach.module.editor.action.ActionGroup
import ink.ptms.adyeshach.module.editor.action.SimpleAction
import ink.ptms.adyeshach.module.editor.action.SimpleGroup
import ink.ptms.adyeshach.module.editor.format
import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.page.PageMove
 *
 * @author 坏黑
 * @since 2022/12/19 18:30
 */
class PageMove(editor: EditPanel) : MultiplePage(editor) {

    override fun subpage() = "move"

    override fun groups(): List<ActionGroup> {
        return listOf(
            SimpleGroup(
                "move-position", 8, listOf(
                    SimpleAction.Literal("&a${entity.x.format()}"),
                    SimpleAction.Literal("&a${entity.y.format()}"),
                    SimpleAction.Literal("&a${entity.z.format()}"),
                    SimpleAction.Literal("&a${entity.yaw.format()}&7, &a${entity.pitch.format()}"),
                    object : SimpleAction.Literal("&7COPY") {

                        override fun clickCommand(player: Player, entity: EntityInstance, page: Page, index: Int): String {
                            return ">${entity.world.name} ${entity.x.format()} ${entity.y.format()} ${entity.z.format()} ${entity.yaw.format()} ${entity.pitch.format()}"
                        }

                        override fun isRefreshPage(): Boolean {
                            return false
                        }
                    }
                )
            ),
            SimpleGroup("move-xyz", 8, listOf(Type.X.actions(), Type.Y.actions(), Type.Z.actions()).flatten()),
            SimpleGroup("move-yp", 8, listOf(Type.YAW.actions(), Type.PITCH.actions()).flatten()),
        )
    }

    enum class Type {

        X, Y, Z, YAW, PITCH;

        fun actions(): List<Action> {
            return listOf(1.0, 0.5, 0.1, 0.01, -1.00, -0.50, -0.10, -0.01).map { Move(it, this) }
        }
    }

    class Move(val value: Double, val type: Type) : SimpleAction.Literal(if (value > 0) "&a+${value.format()}" else "&c${value.format()}", null) {

        override fun isCustomCommand(): Boolean {
            return true
        }

        override fun clickCommand(player: Player, entity: EntityInstance, page: Page, index: Int): String {
            return when (type) {
                Type.X -> "adyeshach tp ${entity.uniqueId} to ~ ~$value ~ ~ ~ ~"
                Type.Y -> "adyeshach tp ${entity.uniqueId} to ~ ~ ~$value ~ ~ ~"
                Type.Z -> "adyeshach tp ${entity.uniqueId} to ~ ~ ~ ~$value ~ ~"
                Type.YAW -> "adyeshach tp ${entity.uniqueId} to ~ ~ ~ ~ ~$value ~"
                Type.PITCH -> "adyeshach tp ${entity.uniqueId} to ~ ~ ~ ~ ~ ~$value"
            }
        }
    }
}