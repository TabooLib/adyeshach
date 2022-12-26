package ink.ptms.adyeshach.module.editor.meta.impl

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.module.editor.meta.MetaEditor
import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.meta.impl.MetaEnum
 *
 * @author 坏黑
 * @since 2022/12/27 04:04
 */
class MetaEnum(val key: String, val enumClass: Class<*>) : MetaEditor {

    override fun open(entity: EntityInstance, player: Player) {
        TODO("Not yet implemented")
    }
}