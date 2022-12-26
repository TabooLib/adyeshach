package ink.ptms.adyeshach.module.editor.page

import ink.ptms.adyeshach.module.editor.EditPanel
import ink.ptms.adyeshach.module.editor.action.ActionGroup

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.page.PagePrivateMeta
 *
 * @author 坏黑
 * @since 2022/12/19 18:30
 */
class PagePrivateMeta(editor: EditPanel) : MultiplePage(editor) {

    override fun subpage() = "private-meta"

    override fun groups(): List<ActionGroup> {
        return emptyList()
    }
}