package ink.ptms.adyeshach.core.entity

import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.Editable
 *
 * @author 坏黑
 * @since 2022/6/16 00:16
 */
interface Editable {

    /**
     * 打开编辑器
     */
    fun openEditor(player: Player, forceEdit: Boolean = false)

    /**
     * 获取实体所有可编辑的元数据模型
     */
    fun getEditableEntityMeta(): List<Meta<*>>
}