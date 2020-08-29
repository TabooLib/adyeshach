package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptContext
import ink.ptms.adyeshach.common.script.util.Closables
import ink.ptms.adyeshach.common.util.Tasks
import io.izzel.kether.common.api.*
import java.util.concurrent.CompletableFuture
import java.util.function.Function


/**
 * @author IzzelAliz
 */
class ActionWait(val tick: Long) : QuestAction<Void, ScriptContext> {

    override fun isAsync(): Boolean {
        return true
    }

    override fun process(context: ScriptContext): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        val bukkitTask = Tasks.delay(tick, true) {
            future.complete(null)
        }
        context.addClosable(AutoCloseable {
            bukkitTask.cancel()
        })
        return future
    }

    override fun getDataPrefix(): String {
        return "wait"
    }

    override fun toString(): String {
        return "ActionWait(tick=$tick)"
    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun parser(): QuestActionParser {
            return object : QuestActionParser {

                override fun <T, C : QuestContext> resolve(resolver: QuestResolver<C>): QuestAction<T, C> {
                    return Function<QuestResolver<C>, QuestAction<T, C>> { t -> ActionWait(t.nextDuration() / 50L) as QuestAction<T, C> }.apply(resolver)
                }

                override fun complete(parms: List<String>): List<String> {
                    return KetherCompleters.seq(KetherCompleters.consume()).apply(parms)
                }
            }
        }
    }
}