package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptParser
import io.izzel.kether.common.api.*
import io.izzel.kether.common.loader.MultipleType
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionSet(val key: String, val value: MultipleType) : QuestAction<Void>() {

    @Suppress("UNCHECKED_CAST")
    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        if (value.isNull) {
            context.variables().remove(key)
        } else {
            return value.process(context).thenAccept {
                context.variables().set(key, it)
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    override fun toString(): String {
        return "ActionSet(key='$key', value=$value)"
    }

    companion object {

        @Suppress("UnstableApiUsage")
        fun parser() = ScriptParser.parser {
            ActionSet(it.nextToken(), it.expect("to").nextMultipleType())
        }
    }
}