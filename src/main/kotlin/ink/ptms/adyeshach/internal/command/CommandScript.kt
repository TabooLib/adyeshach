package ink.ptms.adyeshach.internal.command

import com.google.common.collect.ImmutableList
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.script.ScriptContext
import ink.ptms.adyeshach.common.script.ScriptService
import ink.ptms.adyeshach.internal.mirror.Mirror
import io.izzel.taboolib.module.command.base.*
import io.izzel.taboolib.module.tellraw.TellrawJson
import io.izzel.taboolib.util.book.BookFormatter
import io.izzel.taboolib.util.book.builder.PageBuilder
import io.izzel.taboolib.util.chat.ComponentSerializer
import io.izzel.taboolib.util.lite.Numbers
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-05 0:05
 */
@BaseCommand(name = "adyeshachscript", aliases = ["ascript"], permission = "adyeshach.command")
class CommandScript : BaseMainCommand(), Helper {

    @SubCommand(description = "start the adyeshach scripts.")
    var run: BaseSubCommand = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("id") { ScriptService.quests.map { it.value.id } }, Argument("viewer", false))
        }

        override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, s: String, args: Array<String>) {
            val quest = ScriptService.getQuest(args[0])
            if (quest.isPresent) {
                val context = ScriptContext.create(quest.get())
                if (args.size > 1) {
                    context.viewer = Bukkit.getPlayerExact(args[1])
                }
                try {
                    ScriptService.startQuest(context)
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
            return arrayOf(Argument("id") { ScriptService.quests.map { it.value.id } }, Argument("viewer", false))
        }

        override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, s: String, args: Array<String>) {
            val quest = ImmutableList.copyOf(ScriptService.runningQuests.values()).firstOrNull { it.quest.id == args[0] }
            if (quest != null) {
                ScriptService.terminateQuest(quest)
            } else {
                sender.info("Script not running.")
            }
        }
    }

    @SubCommand(description = "view all the adyeshach scripts.")
    var list: BaseSubCommand = object : BaseSubCommand() {

        override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, s: String, args: Array<String>) {
            sender.info("Scripts: &f${ScriptService.quests.map { it.value.id }.joinToString(" ")}")
            val json = TellrawJson.create().append("§c[Adyeshach] §7Scripts Running: §f")
            ScriptService.runningQuests.entries().forEach {
                json.append(it.value.quest.id).hoverText(it.key).append(" ")
            }
            json.send(sender)
        }
    }

    @SubCommand(description = "reload the adyeshach scripts.")
    var reload: BaseSubCommand = object : BaseSubCommand() {

        override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, s: String, args: Array<String>) {
            ScriptService.cancelAll()
            ScriptService.loadAll()
            sender.info("Scripts has been reloaded.")
        }
    }
}