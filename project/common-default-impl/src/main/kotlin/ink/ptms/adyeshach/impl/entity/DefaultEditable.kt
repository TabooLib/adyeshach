package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.common.entity.Editable
import ink.ptms.adyeshach.common.entity.Meta
import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.DefaultEditable
 *
 * @author 坏黑
 * @since 2022/6/19 22:07
 */
interface DefaultEditable : Editable {

    override fun openEditor(player: Player) {
        TODO("Not yet implemented")
    }

    override fun getEditableEntityMeta(): List<Meta<*>> {
        TODO("Not yet implemented")
    }
}