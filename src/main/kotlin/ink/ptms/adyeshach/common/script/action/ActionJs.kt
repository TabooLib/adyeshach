package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptContext
import io.izzel.kether.common.api.*
import io.izzel.taboolib.util.lite.Scripts
import java.util.concurrent.CompletableFuture
import java.util.function.Function
import javax.script.CompiledScript
import javax.script.SimpleBindings

/**
 * @author IzzelAliz
 */
class ActionJs(val script: CompiledScript) : QuestAction<Any, ScriptContext> {

    override fun isAsync(): Boolean {
        return false
    }

    override fun process(context: ScriptContext): CompletableFuture<Any> {
        return CompletableFuture.completedFuture(script.eval(SimpleBindings(mapOf(
                "player" to context.viewer,
                "viewer" to context.viewer,
                "entity" to context.getEntity(),
                "manager" to context.getManager(),
                "data" to context.persistentData,
                "event" to context.currentEvent?.first
        ))))
    }

    override fun getDataPrefix(): String {
        return "js"
    }

    override fun toString(): String {
        return "ActionJs(script=$script)"
    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun parser(): QuestActionParser {
            return object : QuestActionParser {

                override fun <T, C : QuestContext> resolve(resolver: QuestResolver<C>): QuestAction<T, C> {
                    return Function<QuestResolver<C>, QuestAction<T, C>> { t -> ActionJs(Scripts.compile(t.nextElement())) as QuestAction<T, C> }.apply(resolver)
                }

                override fun complete(parms: List<String>): List<String> {
                    return KetherCompleters.seq(KetherCompleters.consume()).apply(parms)
                }
            }
        }
    }
}