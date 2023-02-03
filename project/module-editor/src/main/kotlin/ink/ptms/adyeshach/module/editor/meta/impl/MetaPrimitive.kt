package ink.ptms.adyeshach.module.editor.meta.impl

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.util.sendLang
import ink.ptms.adyeshach.module.editor.clearScreen
import ink.ptms.adyeshach.module.editor.lang
import ink.ptms.adyeshach.module.editor.meta.MetaEditor
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import taboolib.common.util.unsafeLazy
import taboolib.module.chat.uncolored
import taboolib.module.configuration.Type
import taboolib.module.configuration.createLocal
import taboolib.module.nms.inputSign
import taboolib.platform.util.nextChat

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.meta.impl.MetaPrimitive
 *
 * @author 坏黑
 * @since 2022/12/27 04:04
 */
class MetaPrimitive(val key: String) : MetaEditor {

    override fun open(entity: EntityInstance, player: Player, def: String) {
        when (getPreferenceInputType(player)) {
            InputType.SIGN -> {
                player.inputSign(arrayOf(def.replace('§', '&'), "", "", player.lang("input-sign"))) {
                    player.chat("/adyeshach api ee adyeshach edit ${entity.uniqueId} m:$key->${it[0] + it[1] + it[2]}")
                }
            }
            InputType.CHAT -> {
                player.clearScreen()
                player.sendLang("editor-input-chat", def.replace('§', '&'))
                player.nextChat {
                    submit { player.chat("/adyeshach api ee adyeshach edit ${entity.uniqueId} m:$key->${it}") }
                }
            }
        }
    }

    enum class InputType {

        SIGN, CHAT
    }

    companion object {

        val preference by unsafeLazy { createLocal("input-preference.json", type = Type.FAST_JSON) }

        fun getPreferenceInputType(player: Player): InputType {
            return preference.getString("${player.name}.input-type")?.let { InputType.valueOf(it) } ?: InputType.SIGN
        }

        fun setPreferenceInputType(player: Player, type: InputType) {
            preference["${player.name}.input-type"] = type.name
        }
    }
}