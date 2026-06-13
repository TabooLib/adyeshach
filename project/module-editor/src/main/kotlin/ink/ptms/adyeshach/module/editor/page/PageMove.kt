package ink.ptms.adyeshach.module.editor.page

import ink.ptms.adyeshach.core.bukkit.data.EntityPosition
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
        val bodyYawDisplay = entity.displayBodyYaw()
        val groups = mutableListOf<ActionGroup>(
            SimpleGroup(
                "move-position",
                7,
                listOf(
                    SimpleAction.Literal("&a${entity.x.format()}"),
                    SimpleAction.Literal("&a${entity.y.format()}"),
                    SimpleAction.Literal("&a${entity.z.format()}"),
                    SimpleAction.Literal("&a${entity.yaw.format()}"),
                    SimpleAction.Literal("&a${entity.pitch.format()}"),
                    SimpleAction.Literal("&a${bodyYawDisplay.format()}"),
                    object : SimpleAction.Literal("&7COPY") {

                        override fun clickCommand(player: Player, entity: EntityInstance, page: Page, index: Int): String {
                            return ">${entity.world.name} ${entity.x.format()} ${entity.y.format()} ${entity.z.format()} ${entity.yaw.format()} ${entity.pitch.format()}"
                        }

                        override fun isRefreshPage(): Boolean {
                            return false
                        }
                    },
                ),
            ),
        )
        // 身体 yaw 是显式编辑字段，不依赖 nitwit 实体的移动 tick。
        groups += SimpleGroup("move-body-yaw", 8, Type.BODY_YAW.actions())
        groups += SimpleGroup("move-yaw", 8, Type.YAW.actions())
        groups += SimpleGroup("move-pitch", 8, Type.PITCH.actions())
        groups += SimpleGroup("move-xyz", 8, listOf(Type.X.actions(), Type.Y.actions(), Type.Z.actions()).flatten())
        return groups
    }

    enum class Type {

        X, Y, Z, YAW, PITCH, BODY_YAW;

        fun actions(): List<Action> {
            return listOf(10.0, 1.0, 0.1, 0.01, -0.01, -0.1, -1.0, -10.0).map { Move(it, this) }
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
                Type.BODY_YAW -> {
                    val base = entity.displayBodyYaw()
                    val next = EntityPosition.normalizeYaw(base + value.toFloat())
                    "adyeshach edit ${entity.uniqueId} m:body_yaw->$next"
                }
            }
        }
    }
}
