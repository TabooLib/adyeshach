package ink.ptms.adyeshach.common.script.action.npc

import ink.ptms.adyeshach.common.script.ScriptContext
import ink.ptms.adyeshach.common.script.ScriptParser
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.util.LocalizedException
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionPassenger(val symbol: Symbol, val passenger: String?) : QuestAction<Void>() {

    enum class Symbol {

        ADD, REMOVE, RESET
    }

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        val s = (context.context() as ScriptContext)
        if (s.manager == null) {
            throw RuntimeException("No manager selected.")
        }
        if (!s.entitySelected()) {
            throw RuntimeException("No entity selected.")
        }
        s.entities!!.filterNotNull().forEach {
            when (symbol) {
                Symbol.ADD -> {
                    s.manager!!.getEntityById(passenger!!).forEach { e ->
                        it.addPassenger(e)
                    }
                }
                Symbol.REMOVE -> {
                    it.getPassengers().filter { it.id == passenger }.forEach { e ->
                        it.removePassenger(e)
                    }
                }
                Symbol.RESET -> it.clearPassengers()
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    override fun toString(): String {
        return "ActionPassenger(symbol=$symbol, passenger=$passenger)"
    }

    companion object {

        fun parser() = ScriptParser.parser {
            val symbol = when (val type = it.nextToken()) {
                "add" -> Symbol.ADD
                "remove" -> Symbol.REMOVE
                "reset" -> Symbol.RESET
                else -> throw LocalizedException.of("not-passenger-method", type)
            }
            ActionPassenger(symbol, if (symbol != Symbol.RESET) it.nextToken() else null)
        }
    }
}