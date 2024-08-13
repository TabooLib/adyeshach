package ink.ptms.adyeshach.module.editor.meta.impl

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.util.argb
import ink.ptms.adyeshach.core.util.rgb
import ink.ptms.adyeshach.module.editor.lang
import ink.ptms.adyeshach.module.editor.meta.MetaEditor
import org.bukkit.entity.Player
import taboolib.common5.cint
import taboolib.module.nms.inputSign
import java.awt.Color

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.meta.impl.MetaColor
 *
 * @author 坏黑
 * @since 2022/12/27 04:04
 */
class MetaColor(val key: String) : MetaEditor {

    override fun open(entity: EntityInstance, player: Player, def: String) {
        // 输入转数字
        val input = def.cint.coerceAtLeast(0)
        // 获取颜色
        val color = Color(input, isARGB(input))
        // 建议
        val suggest = buildString {
            // 基本值 R-G-B
            append("${color.red}-${color.green}-${color.blue}")
            // 透明度 Alpha
            if (isARGB(input)) {
                append("-${color.alpha}")
            }
        }
        // 书写时将 Alpha 放在最后
        player.inputSign(arrayOf(suggest, "^^^^^^^^^", "R-G-B-A", player.lang("input-color"))) {
            val split = it[0].split("-")
            val r = split[0].cint
            if (r == -1) {
                player.chat("/adyeshach api ee adyeshach edit ${entity.uniqueId} m:$key->@RESET")
            } else {
                val g = split.getOrNull(1)?.cint ?: 0
                val b = split.getOrNull(2)?.cint ?: 0
                val a = split.getOrNull(3)?.cint ?: 0
                player.chat("/adyeshach api ee adyeshach edit ${entity.uniqueId} m:$key->${if (a > 0) argb(a, r, g, b) else rgb(r, g, b)}")
            }
        }
    }

    fun isARGB(color: Int): Boolean {
        return color shr 24 != 0
    }
}