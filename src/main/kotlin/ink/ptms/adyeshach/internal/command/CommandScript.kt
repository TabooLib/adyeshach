package ink.ptms.adyeshach.internal.command

import com.google.common.collect.ImmutableList
import ink.ptms.adyeshach.common.script.ScriptHandler
import io.izzel.taboolib.kotlin.kether.ScriptContext
import io.izzel.taboolib.module.command.base.*
import io.izzel.taboolib.module.tellraw.TellrawJson
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

/**
 * @Author sky
 * @Since 2020-08-05 0:05
 */
@BaseCommand(name = "adyeshachscript", aliases = ["ascript"], permission = "adyeshach.command")
class CommandScript : BaseMainCommand(), Helper {

    val workspace = ScriptHandler.workspace

    @SubCommand(description = "start the adyeshach scripts.")
    var run: BaseSubCommand = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("id") { workspace.scripts.map { it.value.id } }, Argument("viewer", false))
        }

        override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, s: String, args: Array<String>) {
            val script = workspace.scripts[args[0]]
            if (script != null) {
                val context = ScriptContext.create(script) {
                    if (args.size > 1) {
                        this.sender = Bukkit.getPlayerExact(args[1])
                    }
                }
                try {
                    workspace.runScript(args[0], context)
                } catch (t: Throwable) {
                    sender.error("An error occurred while starting the script:")
                    sender.error("${t.message}")
                    t.printStackTrace()
                }
            } else {
                sender.info("Script not found.")
            }
        }
    }

    @SubCommand(description = "terminate the adyeshach scripts.")
    var stop: BaseSubCommand = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("id") { workspace.scripts.map { it.value.id } }, Argument("viewer", false))
        }

        override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, s: String, args: Array<String>) {
            val script = ImmutableList.copyOf(workspace.getRunningScript()).firstOrNull { it.quest.id == args[0] }
            if (script != null) {
                workspace.terminateScript(script)
            } else {
                sender.info("Script not running.")
            }
        }
    }

    @SubCommand(description = "terminate all of the adyeshach scripts.")
    var stopall: BaseSubCommand = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("id", false) { workspace.scripts.map { it.value.id } }, Argument("viewer", false))
        }

        override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, s: String, args: Array<String>) {
            val script = ImmutableList.copyOf(workspace.getRunningScript()).filter { args.isEmpty() || it.quest.id == args[0] }
            if (script.isNotEmpty()) {
                script.forEach { workspace.terminateScript(it) }
            } else {
                sender.info("Script not running.")
            }
        }
    }

    @SubCommand(description = "view all the adyeshach scripts.")
    var list: BaseSubCommand = object : BaseSubCommand() {

        override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, s: String, args: Array<String>) {
            sender.info("Scripts: &f${workspace.scripts.map { it.value.id }.joinToString(" ")}")
            val json = TellrawJson.create().append("§c[Adyeshach] §7Scripts Running: §f")
            workspace.getRunningScript().forEach {
                json.append(it.quest.id).hoverText(it.id).append(" ")
            }
            json.send(sender)
        }
    }

    @SubCommand(description = "reload the adyeshach scripts.")
    var reload: BaseSubCommand = object : BaseSubCommand() {

        override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, s: String, args: Array<String>) {
            workspace.cancelAll()
            workspace.loadAll()
            sender.info("Scripts has been reloaded.")
        }
    }
}