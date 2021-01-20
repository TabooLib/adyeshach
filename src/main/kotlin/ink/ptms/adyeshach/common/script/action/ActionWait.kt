package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptParser
import ink.ptms.adyeshach.common.util.Tasks
import io.izzel.kether.common.api.*
import java.util.concurrent.CompletableFuture


/**
 * @author IzzelAliz
 */
class ActionWait(val ticks: Long) : QuestAction<Void>() {

    override fun process(frame: QuestContext.Frame): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        val bukkitTask = Tasks.delay(ticks, true) {
            future.complete(null)
        }
        frame.addClosable(AutoCloseable {
            bukkitTask.cancel()
        })
        return future
    }

    override fun toString(): String {
        return "ActionWait(tick=$ticks)"
    }

    companion object {

        fun parser() = ScriptParser.parser {
            ActionWait(it.nextDuration().toMillis() / 50L)
        }
    }
}