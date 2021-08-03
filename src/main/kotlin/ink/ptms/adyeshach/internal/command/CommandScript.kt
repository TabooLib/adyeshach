package ink.ptms.adyeshach.internal.command

import ink.ptms.adyeshach.common.script.ScriptHandler
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import taboolib.common.platform.CommandBody
import taboolib.common.platform.CommandHeader
import taboolib.common.platform.adaptCommandSender
import taboolib.common.platform.subCommand
import taboolib.module.kether.ScriptContext
import taboolib.module.kether.printKetherErrorMessage
import taboolib.platform.util.sendLang

@CommandHeader(name = "adyeshachscript", aliases = ["ascript"], permission = "adyeshach.command")
internal object CommandScript {

    val workspace by lazy { ScriptHandler.workspace }

    @CommandBody
    val run = subCommand {
        // script
        dynamic {
            suggestion<CommandSender> { _, _ ->
                workspace.scripts.map { it.value.id }
            }
            // viewer
            dynamic(optional = true) {
                suggestion<CommandSender> { _, _ ->
                    Bukkit.getOnlinePlayers().map { it.name }
                }
                // ver
                dynamic(optional = true) {
                    execute<CommandSender> { sender, context, argument ->
                        commandRun(sender, context.argument(-2)!!, context.argument(-1), argument.split(" ").toTypedArray())
                    }
                }
                execute<CommandSender> { sender, context, argument ->
                    commandRun(sender, context.argument(-1)!!, argument)
                }
            }
            execute<CommandSender> { sender, _, argument ->
                commandRun(sender, argument)
            }
        }
    }

    @CommandBody
    val stop = subCommand {
        dynamic(optional = true) {
            suggestion<CommandSender> { _, _ ->
                workspace.scripts.map { it.value.id }
            }
            execute<CommandSender> { sender, _, argument ->
                val script = workspace.getRunningScript().filter { it.quest.id == argument }
                if (script.isNotEmpty()) {
                    script.forEach { workspace.terminateScript(it) }
                } else {
                    sender.sendLang("command-script-not-running")
                }
            }
        }
        execute<CommandSender> { _, _, _ ->
            workspace.getRunningScript().forEach { workspace.terminateScript(it) }
        }
    }

    @CommandBody
    val list = subCommand {
        execute<CommandSender> { sender, _, _ ->
            sender.sendLang("command-script-list-all",
                workspace.scripts.map { it.value.id }.joinToString(", "),
                workspace.getRunningScript().joinToString(", ") { it.id }
            )
        }
    }

    @CommandBody
    val reload = subCommand {
        execute<CommandSender> { sender, _, _ ->
            workspace.cancelAll()
            workspace.loadAll()
            sender.sendLang("command-script-reload-all")
        }
    }

    internal fun commandRun(sender: CommandSender, file: String, viewer: String? = null, args: Array<String> = emptyArray()) {
        val script = workspace.scripts[file]
        if (script != null) {
            val context = ScriptContext.create(script) {
                if (viewer != null) {
                    val player = Bukkit.getPlayerExact(viewer)
                    if (player != null) {
                        this.sender = adaptCommandSender(player)
                    }
                }
                var i = 0
                while (i < args.size) {
                    rootFrame().variables().set("arg${i}", args[i])
                    i++
                }
            }
            try {
                workspace.runScript(args[0], context)
            } catch (t: Throwable) {
                sender.sendLang("command-script-error", t.localizedMessage)
                t.printKetherErrorMessage()
            }
        } else {
            sender.sendLang("command-script-not-found")
        }
    }
}