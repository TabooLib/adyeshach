package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.Kether
import ink.ptms.adyeshach.common.script.KnownEvent
import ink.ptms.adyeshach.common.script.ScriptContext
import ink.ptms.adyeshach.common.script.util.Closables
import io.izzel.kether.common.api.*
import io.izzel.kether.common.util.LocalizedException
import java.util.concurrent.CompletableFuture
import java.util.function.Function

/**
 * @author IzzelAliz
 */
class ActionListen(val listen: KnownEvent<*>, val value: QuestAction<Any, QuestContext>) : QuestAction<Void, ScriptContext> {

    override fun isAsync(): Boolean {
        return true
    }

    @Suppress("UNCHECKED_CAST")
    override fun process(context: ScriptContext): CompletableFuture<Void> {
        return CompletableFuture<Void>().also { future ->
            context.persistentData["\$currentListener"] = future
            context.addClosable(Closables.listening(listen.eventClass.java) {
                context.persistentData["\$currentEvent"] = it to listen
                value.process(context)
            })
        }
    }

    override fun getDataPrefix(): String {
        return "listen"
    }

    override fun toString(): String {
        return "ActionListen(listen=$listen, value=$value)"
    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun parser(): QuestActionParser {
            return object : QuestActionParser {

                override fun <T, C : QuestContext> resolve(resolver: QuestResolver<C>): QuestAction<T, C> {
                    return Function<QuestResolver<C>, QuestAction<T, C>> { t ->
                        val name = t.nextElement()
                        val event = Kether.getKnownEvent(name) ?: throw LocalizedException.of("unknown-event", name)
                        t.consume("then")
                        ActionListen(event, t.nextAction<QuestContext>() as QuestAction<Any, QuestContext>) as QuestAction<T, C>
                    }.apply(resolver)
                }

                override fun complete(parms: List<String>): List<String> {
                    return KetherCompleters.seq(KetherCompleters.consume()).apply(parms)
                }
            }
        }
    }
}