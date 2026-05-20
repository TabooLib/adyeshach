package ink.ptms.adyeshach.module.editor.meta.impl

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.util.Components
import ink.ptms.adyeshach.core.util.sendLang
import ink.ptms.adyeshach.module.editor.clearScreen
import ink.ptms.adyeshach.module.editor.meta.MetaEditor
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import taboolib.module.chat.uncolored
import taboolib.platform.util.nextChat

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.meta.impl.MetaText
 *
 * @author 坏黑
 * @since 2022/12/27 04:04
 */
class MetaText(val key: String) : MetaEditor {

    override fun open(entity: EntityInstance, player: Player, def: String) {
        val plainMessage = Components.toLegacyText(def).takeIf { it.uncolored().isNotEmpty() }?.replace("§", "&") ?: ""
        player.clearScreen()
        player.sendLang("editor-input-chat-component", plainMessage, plainMessage, def.replace('§', '&'))
        player.nextChat {
            submit { player.chat("/adyeshach api ee adyeshach edit ${entity.uniqueId} m:$key->${it}") }
        }
    }
}