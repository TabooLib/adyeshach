package ink.ptms.adyeshach.module.editor.page

import ink.ptms.adyeshach.module.editor.EditPanel
import ink.ptms.adyeshach.module.editor.action.ActionGroup

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.page.PagePublicMeta
 *
 * @author 坏黑
 * @since 2022/12/19 18:30
 */
class PagePublicMeta(editor: EditPanel) : MultiplePage(editor) {

    override fun subpage() = "public-meta"

    override fun groups(): List<ActionGroup> {
        return emptyList()
    }
}