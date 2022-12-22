package ink.ptms.adyeshach.module.editor.action

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.action.ActionGroup
 *
 * @author 坏黑
 * @since 2022/12/22 22:24
 */
interface ActionGroup {

    fun id(): String

    fun actionPerLine(): Int
}