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

    /** 序号 */
    fun id(): String

    /** 展示文本 */
    fun display(player: Player): String

    /** 描述文本 */
    fun description(player: Player): String? {
        return null
    }

    /** 是否支持重置 */
    fun isResettable(): Boolean {
        return false
    }

    /** 构建命令 */
    fun clickCommand(player: Player, entity: EntityInstance, page: Page, index: Int): String? {
        return null
    }

    /** 自定义命令 */
    fun isCustomCommand(): Boolean {
        return false
    }

    /** 是否刷新页面 */
    fun isRefreshPage(): Boolean {
        return true
    }
}