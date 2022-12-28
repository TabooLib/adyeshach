package ink.ptms.adyeshach.module.editor.meta.impl

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.module.editor.lang
import ink.ptms.adyeshach.module.editor.meta.MetaEditor
import org.bukkit.entity.Player
import taboolib.library.xseries.XMaterial
import taboolib.module.nms.inputSign
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.Slots
import taboolib.platform.util.buildItem
import taboolib.platform.util.nextChat
import ink.ptms.adyeshach.core.util.sendLang

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.meta.impl.MetaPrimitive
 *
 * @author 坏黑
 * @since 2022/12/27 04:04
 */
class MetaPrimitive(val key: String) : MetaEditor {

    override fun open(entity: EntityInstance, player: Player, def: String) {
        player.openMenu<Basic>(player.lang("input-primitive")) {
            rows(3)
            set(Slots.LINE_2_MIDDLE - 1, buildItem(XMaterial.OAK_SIGN) {
                name = player.lang("input-primitive-sign")
                lore += player.lang("input-primitive-sign-description")
            }) {
                // 牌子输入
                player.inputSign(arrayOf(def, "", "", player.lang("input-sign"))) {
                    player.chat("/adyeshach api ee adyeshach edit ${entity.uniqueId} m:$key->${it[0] + it[1] + it[2]}")
                }
            }
            set(Slots.LINE_2_MIDDLE + 1, buildItem(XMaterial.PLAYER_HEAD) {
                name = player.lang("input-primitive-chat")
            }) {
                // 聊天输入
                language.sendLang(player, "editor-input-chat", def)
                player.nextChat {
                    player.chat("/adyeshach api ee adyeshach edit ${entity.uniqueId} m:$key->${it}")
                }
            }
        }
    }
}