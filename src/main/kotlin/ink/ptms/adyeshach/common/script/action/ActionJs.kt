package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptContext
import ink.ptms.adyeshach.common.script.ScriptParser
import io.izzel.kether.common.api.*
import io.izzel.taboolib.util.Features
import java.util.concurrent.CompletableFuture
import javax.script.CompiledScript
import javax.script.SimpleBindings

/**
 * @author IzzelAliz
 */
class ActionJs(val script: CompiledScript) : QuestAction<Any>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Any> {
        val s = (context.context() as ScriptContext)
        return CompletableFuture.completedFuture(
            script.eval(
                SimpleBindings(
                    mapOf(
                        "player" to s.viewer,
                        "viewer" to s.viewer,
                        "entity" to s.entities?.firstOrNull(),
                        "entities" to s.entities,
                        "manager" to s.manager,
                        "data" to context.variables().values().map { it.key to it.value }.toMap(),
                        "event" to s.currentEvent?.first
                    )
                )
            )
        )
    }

    override fun toString(): String {
        return "ActionJs(script=$script)"
    }

    companion object {

        fun parser() = ScriptParser.parser {
            ActionJs(Features.compileScript(it.nextToken().trimIndent())!!)
        }
    }
}