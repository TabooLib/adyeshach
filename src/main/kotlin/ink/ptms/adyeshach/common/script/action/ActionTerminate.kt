package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptContext
import ink.ptms.adyeshach.common.script.ScriptParser
import ink.ptms.adyeshach.common.script.ScriptService
import io.izzel.kether.common.api.*
import java.util.concurrent.CompletableFuture


/**
 * @author IzzelAliz
 */
class ActionTerminate : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        ScriptService.terminateQuest(context.context() as ScriptContext)
        return CompletableFuture.completedFuture(null)
    }

    override fun toString(): String {
        return "ActionTerminate()"
    }

    companion object {

        fun parser() = ScriptParser.parser {
            ActionTerminate()
        }
    }
}