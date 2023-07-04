package ink.ptms.adyeshach.module.command

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.util.sendLang
import ink.ptms.adyeshach.impl.DefaultScriptManager
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.adaptCommandSender
import taboolib.expansion.createHelper
import taboolib.library.reflex.Reflex.Companion.invokeMethod
import taboolib.module.kether.Kether
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptContext
import taboolib.module.kether.printKetherErrorMessage

@CommandHeader(name = "adyeshachscript", aliases = ["ascript"], permission = "adyeshach.command")
object CommandScript {

    val workspace by lazy { DefaultScriptManager.workspace }

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody
    val run = subCommand {
        // script
        dynamic(comment = "file") {
            suggestion<CommandSender> { _, _ ->
                workspace.scripts.map { it.value.id }
            }
            // viewer
            dynamic(comment = "viewer", optional = true) {
                suggestion<CommandSender> { _, _ ->
                    Bukkit.getOnlinePlayers().map { it.name }
                }
                // ver
                dynamic(comment = "args", optional = true) {
                    execute<CommandSender> { sender, context, argument ->
                        commandRun(sender, context.argument(-2), context.argument(-1), argument.split(" ").toTypedArray())
                    }
                }
                execute<CommandSender> { sender, context, argument ->
                    commandRun(sender, context.argument(-1), argument)
                }
            }
            execute<CommandSender> { sender, _, argument ->
                commandRun(sender, argument)
            }
        }
    }

    @CommandBody
    val stop = subCommand {
        dynamic(comment = "file", optional = true) {
            suggestion<CommandSender> { _, _ ->
                workspace.scripts.map { it.value.id }
            }
            execute<CommandSender> { sender, _, argument ->
                if (!sender.isOp) {
                    sender.sendMessage("§c§l[Adyeshach] §7You do not have permission.")
                }
                val script = workspace.getRunningScript().filter { it.quest.id == argument }
                if (script.isNotEmpty()) {
                    script.forEach { workspace.terminateScript(it) }
                } else {
                    sender.sendLang("command-script-not-running")
                }
            }
        }
        execute<CommandSender> { sender, _, _ ->
            if (!sender.isOp) {
                sender.sendMessage("§c§l[Adyeshach] §7You do not have permission.")
            }
            workspace.getRunningScript().forEach { workspace.terminateScript(it) }
        }
    }

    @CommandBody
    val list = subCommand {
        execute<CommandSender> { sender, _, _ ->
            if (!sender.isOp) {
                sender.sendMessage("§c§l[Adyeshach] §7You do not have permission.")
            }
            sender.sendLang("command-script-list-all",
                workspace.scripts.map { it.value.id }.joinToString(", "),
                workspace.getRunningScript().joinToString(", ") { it.id }
            )
        }
    }

    @CommandBody
    val reload = subCommand {
        execute<CommandSender> { sender, _, _ ->
            if (!sender.isOp) {
                sender.sendMessage("§c§l[Adyeshach] §7You do not have permission.")
            }
            workspace.cancelAll()
            workspace.loadAll()
            sender.sendLang("command-script-reload-all")
        }
    }

    @CommandBody
    val debug = subCommand {
        execute<CommandSender> { sender, _, _ ->
            if (!sender.isOp) {
                sender.sendMessage("§c§l[Adyeshach] §7You do not have permission.")
            }
            sender.sendMessage(" §5§l‹ ›§r §7RegisteredActions:")
            Kether.scriptRegistry.registeredNamespace.forEach {
                sender.sendMessage(" §5§l‹ ›§r §7  ${it}: §r${Kether.scriptRegistry.getRegisteredActions(it)}")
            }
        }
    }

    @CommandBody
    val invoke = subCommand {
        dynamic(comment = "script") {
            execute<CommandSender> { sender, _, argument ->
                if (!sender.isOp) {
                    sender.sendMessage("§c§l[Adyeshach] §7You do not have permission.")
                }
                // 获取附近的 NPC
                val nearestEntity = if (sender is Player) {
                    val entity = Adyeshach.api().getEntityFinder().getNearestEntity(sender) { !it.isDerived() }
                    if (entity != null) listOf(entity) else null
                } else {
                    null
                }
                try {
                    KetherShell.eval(
                        argument,
                        namespace = listOf("adyeshach"),
                        sender = adaptCommandSender(sender),
                        vars = KetherShell.VariableMap("@entities" to nearestEntity, "@manager" to nearestEntity?.firstOrNull()?.manager)
                    ).thenApply { v ->
                        try {
                            Class.forName(v.toString().substringBefore('$'))
                            sender.sendMessage(" §5§l‹ ›§r §7Result: §f${v!!.javaClass.simpleName} §7(Java Object)")
                        } catch (_: Throwable) {
                            sender.sendMessage(" §5§l‹ ›§r §7Result: §f$v")
                        }
                    }
                } catch (ex: Throwable) {
                    sender.sendMessage(" §5§l‹ ›§r §7Error: ${ex.message}")
                    ex.printKetherErrorMessage()
                }
            }
        }
    }

    fun commandRun(sender: CommandSender, file: String, viewer: String? = null, args: Array<String> = emptyArray()) {
        if (!sender.isOp) {
            sender.sendMessage("§c§l[Adyeshach] §7You do not have permission.")
        }
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
                workspace.runScript(file, context)
            } catch (t: Throwable) {
                sender.sendLang("command-script-error", t.localizedMessage)
                t.printKetherErrorMessage()
            }
        } else {
            sender.sendLang("command-script-not-found")
        }
    }
}