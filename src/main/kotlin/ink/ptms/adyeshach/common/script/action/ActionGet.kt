package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptParser
import io.izzel.kether.common.api.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException

/**
 * @author IzzelAliz
 */
class ActionGet(val key: String) : QuestAction<Any?>() {

    @Suppress("UNCHECKED_CAST")
    override fun process(context: QuestContext.Frame): CompletableFuture<Any?> {
        return try {
            CompletableFuture.completedFuture(context.variables().get<Any>(key).orElse(null))
        } catch (e: CompletionException) {
            e.printStackTrace()
            CompletableFuture.completedFuture(null)
        }
    }

    override fun toString(): String {
        return "ActionGet(key='$key')"
    }

    companion object {

        @Suppress("UnstableApiUsage")
        fun parser() = ScriptParser.parser {
            ActionGet(it.nextToken())
        }
    }
}