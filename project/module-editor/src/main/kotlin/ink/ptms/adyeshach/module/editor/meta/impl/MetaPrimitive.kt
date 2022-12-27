package ink.ptms.adyeshach.module.editor.meta.impl

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.module.editor.lang
import ink.ptms.adyeshach.module.editor.meta.MetaEditor
import org.bukkit.entity.Player
import taboolib.module.nms.inputSign

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.meta.impl.MetaPrimitive
 *
 * @author 坏黑
 * @since 2022/12/27 04:04
 */
class MetaPrimitive(val key: String) : MetaEditor {

    override fun open(entity: EntityInstance, player: Player, def: String) {
        player.inputSign(arrayOf(def, "", "", player.lang("input-sign"))) {
            player.chat("/adyeshach api ee adyeshach edit ${entity.uniqueId} m:$key->${it[0] + it[1] + it[2]}")
        }
    }
}