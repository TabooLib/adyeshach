package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptContext
import ink.ptms.adyeshach.common.util.Tasks
import io.izzel.kether.common.api.*
import org.bukkit.Bukkit
import java.util.concurrent.CompletableFuture
import java.util.function.Function

/**
 * @author IzzelAliz
 */
class ActionCommand(val command: String) : QuestAction<Void, ScriptContext> {

    override fun isAsync(): Boolean {
        return false
    }

    override fun process(context: ScriptContext): CompletableFuture<Void> {
        Tasks.task {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("@player", context.viewer?.name.toString()))
        }
        return CompletableFuture.completedFuture(null)
    }

    override fun getDataPrefix(): String {
        return "command"
    }

    override fun toString(): String {
        return "ActionCommand{" +
                "command='" + command + '\'' +
                '}'
    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun parser(): QuestActionParser {
            return object : QuestActionParser {

                override fun <T, C : QuestContext> resolve(resolver: QuestResolver<C>): QuestAction<T, C> {
                    return Function<QuestResolver<C>, QuestAction<T, C>> { t -> ActionCommand(t.nextElement()) as QuestAction<T, C> }.apply(resolver)
                }

                override fun complete(parms: List<String>): List<String> {
                    return KetherCompleters.seq(KetherCompleters.consume()).apply(parms)
                }
            }
        }
    }
}