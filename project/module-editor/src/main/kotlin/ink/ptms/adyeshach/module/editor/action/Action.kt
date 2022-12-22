package ink.ptms.adyeshach.module.editor.action

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.module.editor.page.Page
import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.action.Action
 *
 * @author 坏黑
 * @since 2022/12/22 21:10
 */
interface Action {

    fun id(): String

    fun isResettable(): Boolean

    fun onClick(player: Player, entity: EntityInstance, page: Page, index: Int)
}