package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.core.AdyeshachLanguage
import org.bukkit.command.CommandSender
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.PlatformFactory
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

    companion object {

        @Awake(LifeCycle.LOAD)
        fun init() {
            PlatformFactory.registerAPI<AdyeshachLanguage>(DefaultAdyeshachLanguage())
        }
    }
}