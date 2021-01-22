package ink.ptms.adyeshach.common.script.action.bukkit

import ink.ptms.adyeshach.common.script.*
import io.izzel.kether.common.api.*
import io.izzel.kether.common.loader.MultipleType
import io.izzel.kether.common.util.LocalizedException
import org.bukkit.event.Event
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionEvent(val key: String, val symbol: Symbol, val value: MultipleType) : QuestAction<Any?>() {

    enum class Symbol {

        GET, SET
    }

    @Suppress("UNCHECKED_CAST")
    override fun process(context: QuestContext.Frame): CompletableFuture<Any?> {
        val currentEvent = (context.context() as ScriptContext).currentEvent
        return if (symbol == Symbol.SET) {
            value.process(context).thenApply {
                val event = currentEvent?.first
                val known = currentEvent?.second as? KnownEvent<Event>
                if (event != null && known != null) {
                    known.field[key]?.second?.func?.invoke(event, it)
                }
            }
        } else {
            val event = currentEvent?.first
            val known = currentEvent?.second as? KnownEvent<Event>
            if (event != null && known != null) {
                CompletableFuture.completedFuture(known.field[key]?.first?.func?.invoke(event))
            } else {
                CompletableFuture.completedFuture(null)
            }
        }
    }

    override fun toString(): String {
        return "ActionEvent(key='$key', symbol=$symbol, value=$value)"
    }

    companion object {

        @Suppress("UnstableApiUsage")
        fun parser() = ScriptParser.parser {
            val symbol = when (val type = it.nextToken()) {
                "set" -> Symbol.SET
                "get" -> Symbol.GET
                else -> throw LocalizedException.of("not-event-method", type)
            }
            val key = it.nextToken()
            val value = it.nextMultipleType()
            ActionEvent(key, symbol, value)
        }
    }
}