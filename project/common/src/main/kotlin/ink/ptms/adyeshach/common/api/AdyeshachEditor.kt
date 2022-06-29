package ink.ptms.adyeshach.common.api

import ink.ptms.adyeshach.common.entity.EntityInstance
import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.api.AdyeshachEditor
 *
 * @author 坏黑
 * @since 2022/6/29 23:53
 */
interface AdyeshachEditor {

    /**
     * 打开编辑器
     */
    fun openEditor(player: Player, entityInstance: EntityInstance)
}