package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptParser
import io.izzel.kether.common.api.*
import java.util.concurrent.CompletableFuture


/**
 * @author IzzelAliz
 */
class ActionPause : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        return CompletableFuture<Void>()
    }

    companion object {

        fun parser() = ScriptParser.parser {
            ActionPause()
        }
    }
}