package ink.ptms.adyeshach.module.lang

import ink.ptms.adyeshach.core.AdyeshachLanguage
import org.bukkit.command.CommandSender
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.PlatformFactory
import taboolib.module.lang.Language
import taboolib.platform.util.asLangTextList
import taboolib.platform.util.asLangTextOrNull
import taboolib.platform.util.sendLang

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.lang.DefaultAdyeshachLanguage
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

    override fun getLangList(sender: CommandSender, key: String, vararg args: Any): List<String> {
        return sender.asLangTextList(key, *args)
    }

    companion object {

        @Awake(LifeCycle.CONST)
        fun init() {
            // 注册服务
            PlatformFactory.registerAPI<AdyeshachLanguage>(DefaultAdyeshachLanguage())
            // 设置语言文件路径
            Language.path = "core/lang"
        }
    }
}