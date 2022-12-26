package ink.ptms.adyeshach.module.editor

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.module.editor.page.PageMain
import ink.ptms.adyeshach.module.editor.page.PageMove
import ink.ptms.adyeshach.module.editor.page.PagePrivateMeta
import ink.ptms.adyeshach.module.editor.page.PageTraits
import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.ChatEditor
 *
 * @author 坏黑
 * @since 2022/12/17 14:16
 */
class EditPanel(val player: Player, val entity: EntityInstance) {

    fun open(type: EditPanelType = EditPanelType.MAIN, page: Int = 0) {
        when (type) {
            EditPanelType.MAIN -> PageMain(this).open(page)
            EditPanelType.TRAITS -> PageTraits(this).open(page)
            EditPanelType.PUBLIC_META -> PagePrivateMeta(this).open(page)
            EditPanelType.PRIVATE_META -> PagePrivateMeta(this).open(page)
            EditPanelType.MOVE -> PageMove(this).open(page)
        }
    }
}