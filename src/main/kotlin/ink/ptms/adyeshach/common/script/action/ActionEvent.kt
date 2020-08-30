package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.KnownEvent
import ink.ptms.adyeshach.common.script.ScriptContext
import ink.ptms.adyeshach.common.script.ScriptResolver
import io.izzel.kether.common.api.*
import io.izzel.kether.common.util.LocalizedException
import org.bukkit.event.Event
import java.util.concurrent.CompletableFuture
import java.util.function.Function

/**
 * @author IzzelAliz
 */
class ActionEvent(val key: String, val symbol: Symbol, val value: Any?) : QuestAction<Any?, ScriptContext> {

    enum class Symbol {

        GET, SET
    }

    override fun isAsync(): Boolean {
        return false
    }

    @Suppress("UNCHECKED_CAST")
    override fun process(context: ScriptContext): CompletableFuture<Any?> {
        if (symbol == Symbol.SET) {
            when (value) {
                is QuestAction<*, *> -> {
                    CompletableFuture<Void>().also { future ->
                        context.runAction("set", value).thenAccept {
                            val event = context.getCurrentEvent()?.first
                            val known = context.getCurrentEvent()?.second
                            if (event != null && known != null) {
                                (known as KnownEvent<Event>).field[key]?.value?.invoke(event, it)
                            }
                            future.complete(null)
                        }
                    }
                }
                else -> {
                    val event = context.getCurrentEvent()?.first
                    val known = context.getCurrentEvent()?.second
                    if (event != null && known != null) {
                        (known as KnownEvent<Event>).field[key]?.value?.invoke(event, if (value == "null") null else value)
                    }
                }
            }
            return CompletableFuture.completedFuture(null)
        } else {
            val event = context.getCurrentEvent()?.first
            val known = context.getCurrentEvent()?.second
            if (event != null && known != null) {
                return CompletableFuture.completedFuture((known as KnownEvent<Event>).field[key]?.key?.invoke(event))
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    override fun getDataPrefix(): String {
        return "event"
    }

    override fun toString(): String {
        return "ActionEvent(key='$key', symbol=$symbol, value=$value)"
    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun parser(): QuestActionParser {
            return object : QuestActionParser {

                override fun <T, C : QuestContext> resolve(resolver: QuestResolver<C>): QuestAction<T, C> {
                    return Function<QuestResolver<C>, QuestAction<T, C>> { t ->
                        val symbol = when (val type = t.nextElement()) {
                            "set" -> Symbol.SET
                            "get" -> Symbol.GET
                            else -> throw LocalizedException.of("not-event-method", type)
                        }
                        val key = t.nextElement()
                        val value = if (symbol == Symbol.SET) try {
                            t.consume("to")
                            t.mark()
                            t.nextAction<QuestContext>()
                        } catch (ignore: Throwable) {
                            t.reset()
                            (t as ScriptResolver<C>).nextAny()
                        } else null
                        ActionEvent(key, symbol, value) as QuestAction<T, C>
                    }.apply(resolver)
                }

                override fun complete(parms: List<String>): List<String> {
                    return KetherCompleters.seq(KetherCompleters.consume()).apply(parms)
                }
            }
        }
    }
}