package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.common.entity.Editable
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.Meta
import ink.ptms.adyeshach.common.entity.Metaable
import ink.ptms.adyeshach.module.AdyeshachEditor
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
        this as EntityInstance
        AdyeshachEditor.openEditor(player, this)
    }

    override fun getEditableEntityMeta(): List<Meta<*>> {
        this as Metaable
        return getAvailableEntityMeta().filter { it.index != -1 }.toList()
    }
}