package ink.ptms.adyeshach.core

import ink.ptms.adyeshach.core.entity.EntityInstance
import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.AdyeshachEditor
 *
 * @author 坏黑
 * @since 2022/6/29 23:53
 */
interface AdyeshachEditor {

    /**
     * 打开编辑器
     */
    fun openEditor(player: Player, entityInstance: EntityInstance, forceEdit: Boolean = false)
}