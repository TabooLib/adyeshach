package ink.ptms.adyeshach.module.editor.action

import kotlin.math.ceil

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.action.SimpleGroup
 *
 * @author 坏黑
 * @since 2022/12/27 00:53
 */
open class SimpleGroup(val id: String, val actionPerLine: Int, val actions: List<Action>) : ActionGroup {

    override fun id(): String {
        return id
    }

    override fun actionPerLine(): Int {
        return actionPerLine
    }

    override fun length(): Int {
        val len = ceil(actions.size / actionPerLine.toDouble()).toInt() + 2
        if (len > 10) {
            error("ActionGroup length cannot exceed 10")
        }
        return len
    }

    override fun actions(): List<Action> {
        return actions
    }

    class Extras(id: String, actions: List<Action>) : SimpleGroup(id, 999, actions)
}