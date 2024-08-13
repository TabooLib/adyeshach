package ink.ptms.adyeshach.module.editor.meta.impl

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.module.editor.lang
import ink.ptms.adyeshach.module.editor.meta.MetaEditor
import org.bukkit.Color
import org.bukkit.entity.Player
import taboolib.common5.cint
import taboolib.module.nms.inputSign

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.meta.impl.MetaColor
 *
 * @author 坏黑
 * @since 2022/12/27 04:04
 */
class MetaColor(val key: String) : MetaEditor {

    override fun open(entity: EntityInstance, player: Player, def: String) {
        val fromRGB = Color.fromRGB(def.cint.coerceAtLeast(0))
        player.inputSign(arrayOf("${fromRGB.red}-${fromRGB.green}-${fromRGB.blue}", "", "", player.lang("input-color"))) {
            val split = it[0].split("-")
            val r = split[0].cint
            val g = split.getOrNull(1)?.cint ?: 0
            val b = split.getOrNull(2)?.cint ?: 0
            val toRGB = if (r >= 0) Color.fromRGB(r, g, b).asRGB() else -1
            player.chat("/adyeshach api ee adyeshach edit ${entity.uniqueId} m:$key->$toRGB")
        }
    }
}