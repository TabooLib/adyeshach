package ink.ptms.adyeshach.common.script.action.npc

import ink.ptms.adyeshach.common.script.ScriptContext
import ink.ptms.adyeshach.common.script.ScriptParser
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.util.LocalizedException
import org.bukkit.Bukkit
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionViewer(val symbol: Symbol, val viewer: String?) : QuestAction<Void>() {

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
                    Bukkit.getPlayerExact(viewer!!)?.run {
                        it.addViewer(this)
                    }
                }
                Symbol.REMOVE -> {
                    Bukkit.getPlayerExact(viewer!!)?.run {
                        it.removeViewer(this)
                    }
                }
                Symbol.RESET -> it.clearViewer()
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    override fun toString(): String {
        return "ActionViewer(symbol=$symbol, viewer=$viewer)"
    }

    companion object {

        fun parser() = ScriptParser.parser {
            val symbol = when (val type = it.nextToken()) {
                "add" -> Symbol.ADD
                "remove" -> Symbol.REMOVE
                "reset" -> Symbol.RESET
                else -> throw LocalizedException.of("not-viewer-method", type)
            }
            ActionViewer(symbol, if (symbol != Symbol.RESET) it.nextToken() else null)
        }
    }
}