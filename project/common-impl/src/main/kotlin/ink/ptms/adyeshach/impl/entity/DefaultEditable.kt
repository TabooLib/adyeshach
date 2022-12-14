package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.Editable
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.Meta
import ink.ptms.adyeshach.core.entity.Metaable
import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.DefaultEditable
 *
 * @author 坏黑
 * @since 2022/6/19 22:07
 */
interface DefaultEditable : Editable {

    override fun openEditor(player: Player, forceEdit: Boolean) {
        this as EntityInstance
        Adyeshach.editor()?.openEditor(player, this, forceEdit)
    }

    override fun getEditableEntityMeta(): List<Meta<*>> {
        this as Metaable
        return getAvailableEntityMeta().filter { it.index != -1 }.toList()
    }
}