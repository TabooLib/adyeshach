package ink.ptms.adyeshach.module.editor.meta.impl

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.module.editor.meta.MetaEditor
import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.meta.impl.MetaVector
 *
 * @author 坏黑
 * @since 2022/12/27 04:04
 */
class MetaVector(val key: String) : MetaEditor {

    override fun open(entity: EntityInstance, player: Player, def: String) {
        val loc = player.location
        player.chat("/adyeshach api ee adyeshach edit ${entity.uniqueId} m:$key->${loc.blockX},${loc.blockY},${loc.blockZ}")
    }
}