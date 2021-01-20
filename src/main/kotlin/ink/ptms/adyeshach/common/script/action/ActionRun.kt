package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptParser
import io.izzel.kether.common.api.*
import io.izzel.kether.common.loader.MultipleType
import java.util.concurrent.CompletableFuture


/**
 * @author IzzelAliz
 */
class ActionRun(val run: MultipleType) : QuestAction<Any>() {

    override fun process(frame: QuestContext.Frame): CompletableFuture<Any> {
        return run.process(frame)
    }

    override fun toString(): String {
        return "ActionRun(run=$run)"
    }

    companion object {

        @Suppress("UnstableApiUsage")
        fun parser() = ScriptParser.parser {
            ActionRun(it.nextMultipleType())
        }
    }
}