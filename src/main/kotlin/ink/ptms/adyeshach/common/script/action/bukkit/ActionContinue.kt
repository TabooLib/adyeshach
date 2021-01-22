package ink.ptms.adyeshach.common.script.action.bukkit

import ink.ptms.adyeshach.common.script.ScriptContext
import ink.ptms.adyeshach.common.script.ScriptParser
import io.izzel.kether.common.api.*
import java.util.concurrent.CompletableFuture


/**
 * @author IzzelAliz
 */
class ActionContinue : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        (context.context() as ScriptContext).currentListener?.complete(null)
        return CompletableFuture.completedFuture(null)
    }

    override fun toString(): String {
        return "ActionContinue()"
    }

    companion object {

        fun parser() = ScriptParser.parser {
            ActionContinue()
        }
    }
}