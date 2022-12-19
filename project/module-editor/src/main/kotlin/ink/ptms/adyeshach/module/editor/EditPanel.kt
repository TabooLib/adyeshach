package ink.ptms.adyeshach.module.editor

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.module.editor.page.PageMain
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
            EditPanelType.MAIN -> PageMain(this).open()
            EditPanelType.TRAITS -> TODO()
            EditPanelType.PUBLIC_META -> TODO()
            EditPanelType.PRIVATE_META -> TODO()
            EditPanelType.MOVE -> TODO()
        }
    }
}