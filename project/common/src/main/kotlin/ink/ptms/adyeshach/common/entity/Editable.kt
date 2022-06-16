package ink.ptms.adyeshach.common.entity

import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.entity.Editable
 *
 * @author 坏黑
 * @since 2022/6/16 00:16
 */
interface Editable {

    /**
     * 打开编辑器
     */
    fun openEditor(player: Player)
}