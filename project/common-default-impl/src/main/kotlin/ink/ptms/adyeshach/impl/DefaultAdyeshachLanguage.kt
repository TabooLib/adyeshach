package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.common.api.AdyeshachLanguage
import org.bukkit.command.CommandSender
import taboolib.platform.util.asLangTextOrNull
import taboolib.platform.util.sendLang

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.DefaultAdyeshachLanguage
 *
 * @author 坏黑
 * @since 2022/6/28 13:32
 */
class DefaultAdyeshachLanguage : AdyeshachLanguage {

    override fun sendLang(sender: CommandSender, key: String, vararg args: Any) {
        sender.sendLang(key, *args)
    }

    override fun getLang(sender: CommandSender, key: String, vararg args: Any): String? {
        return sender.asLangTextOrNull(key, *args)
    }
}