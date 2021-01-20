package ink.ptms.adyeshach.common.script.action.npc

import ink.ptms.adyeshach.common.script.ScriptContext
import ink.ptms.adyeshach.common.script.ScriptParser
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionDestroy : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        val s = (context.context() as ScriptContext)
        if (s.manager == null) {
            throw RuntimeException("No manager selected.")
        }
        if (!s.entitySelected()) {
            throw RuntimeException("No entity selected.")
        }
        s.entities!!.filterNotNull().forEach {
            it.destroy()
        }
        return CompletableFuture.completedFuture(null)
    }

    override fun toString(): String {
        return "ActionDestroy()"
    }

    companion object {

        fun parser() = ScriptParser.parser {
            ActionDestroy()
        }
    }
}