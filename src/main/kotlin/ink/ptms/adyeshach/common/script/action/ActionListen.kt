package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.*
import ink.ptms.adyeshach.common.script.util.Closables
import io.izzel.kether.common.api.*
import io.izzel.kether.common.loader.MultipleType
import io.izzel.kether.common.util.LocalizedException
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionListen(val listen: KnownEvent<*>, val value: MultipleType) : QuestAction<Void>() {

    @Suppress("UNCHECKED_CAST")
    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        return CompletableFuture<Void>().also { future ->
            val s = (context.context() as ScriptContext)
            s.currentListener = future
            context.addClosable(Closables.listening(listen.eventClass.java) {
                s.currentEvent = it to listen
                value.process(context)
            })
        }
    }

    override fun toString(): String {
        return "ActionListen(listen=$listen, value=$value)"
    }

    companion object {

        @Suppress("UnstableApiUsage")
        fun parser() = ScriptParser.parser {
            val name = it.nextToken()
            val event = ScriptHandler.getKnownEvent(name) ?: throw LocalizedException.of("unknown-event", name)
            it.expect("then")
            ActionListen(event, it.nextMultipleType())
        }
    }
}