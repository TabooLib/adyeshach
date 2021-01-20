package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptContext
import ink.ptms.adyeshach.common.script.ScriptParser
import ink.ptms.adyeshach.common.util.Tasks
import io.izzel.kether.common.api.*
import org.bukkit.Bukkit
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionCommand(val command: String) : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        Tasks.task {
            val player = (context.context() as ScriptContext).viewer?.name.toString()
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("@player", player))
        }
        return CompletableFuture.completedFuture(null)
    }

    override fun toString(): String {
        return "ActionCommand{" +
                "command='" + command + '\'' +
                '}'
    }

    companion object {

        fun parser() = ScriptParser.parser {
            ActionCommand(it.nextToken().trimIndent())
        }
    }
}