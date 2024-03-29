package ink.ptms.adyeshach.core

import org.bukkit.command.CommandSender

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.AdyeshachLanguage
 *
 * @author 坏黑
 * @since 2022/6/28 13:30
 */
interface AdyeshachLanguage {

    /**
     * 发送语言文件
     */
    fun sendLang(sender: CommandSender, key: String, vararg args: Any)

    /**
     * 获取语言文件
     */
    fun getLang(sender: CommandSender, key: String, vararg args: Any): String?

    /**
     * 获取语言文件
     */
    fun getLangList(sender: CommandSender, key: String, vararg args: Any): List<String>
}