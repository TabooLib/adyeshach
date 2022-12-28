package ink.ptms.adyeshach.module.command

import ink.ptms.adyeshach.module.editor.ChatEditor
import ink.ptms.adyeshach.module.editor.meta.impl.MetaPrimitive
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.*
import taboolib.common.platform.function.adaptPlayer
import taboolib.expansion.createHelper
import taboolib.platform.util.hasMeta
import taboolib.platform.util.removeMeta
import taboolib.platform.util.setMeta

@CommandHeader(name = "adyeshachapi", aliases = ["aapi"], permission = "adyeshach.command")
object CommandAPI {

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    /**
     * 静默输入
     */
    @CommandBody
    val se = subCommand {
        dynamic("command") {
            execute<Player> { sender, _, args ->
                sender.setMeta("adyeshach_ignore_notice", true)
                try {
                    adaptPlayer(sender).performCommand(args)
                } catch (ex: Throwable) {
                    ex.printStackTrace()
                }
                sender.removeMeta("adyeshach_ignore_notice")
            }
        }
    }

    /**
     * 静默输入并刷新页面
     */
    @CommandBody
    val ee = subCommand {
        dynamic("command") {
            execute<Player> { sender, _, args ->
                sender.setMeta("adyeshach_ignore_notice", true)
                try {
                    adaptPlayer(sender).performCommand(args)
                } catch (ex: Throwable) {
                    ex.printStackTrace()
                }
                ChatEditor.refresh(sender)
                sender.removeMeta("adyeshach_ignore_notice")
            }
        }
    }

    /**
     * 输入设置
     */
    @CommandBody
    val pi = subCommand {
        dynamic("input-type") {
            suggest { listOf("SIGN", "CHAT") }
            execute<Player> { sender, _, args ->
                MetaPrimitive.setPreferenceInputType(sender, MetaPrimitive.InputType.valueOf(args))
                ChatEditor.refresh(sender)
            }
        }
    }
}

fun CommandSender.isIgnoreNotice(): Boolean {
    return this is Player && hasMeta("adyeshach_ignore_notice")
}