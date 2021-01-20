package ink.ptms.adyeshach.common.script.action.npc

import ink.ptms.adyeshach.common.entity.type.AdyHuman
import ink.ptms.adyeshach.common.script.ScriptContext
import ink.ptms.adyeshach.common.script.ScriptParser
import io.izzel.kether.common.api.*
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionSleeping : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        val s = (context.context() as ScriptContext)
        if (s.manager == null) {
            throw RuntimeException("No manager selected.")
        }
        if (!s.entitySelected()) {
            throw RuntimeException("No entity selected.")
        }
        s.entities!!.filterNotNull().forEach {
            if (it is AdyHuman) {
                it.setSleeping(!it.isSleeping())
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    override fun toString(): String {
        return "ActionSleeping()"
    }

    companion object {

        fun parser() = ScriptParser.parser {
            ActionSleeping()
        }
    }
}