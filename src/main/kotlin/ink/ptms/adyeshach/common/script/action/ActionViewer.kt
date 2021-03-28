package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptHandler
import ink.ptms.adyeshach.common.script.ScriptHandler.entitySelected
import ink.ptms.adyeshach.common.script.ScriptHandler.getEntities
import ink.ptms.adyeshach.common.script.ScriptHandler.getManager
import io.izzel.taboolib.kotlin.kether.ScriptContext
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.kether.common.api.ParsedAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestContext
import io.izzel.taboolib.kotlin.kether.common.loader.types.ArgTypes
import org.bukkit.Bukkit
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionViewer(val symbol: Symbol, val viewer: ParsedAction<*>?) : QuestAction<Void>() {

    enum class Symbol {

        ADD, REMOVE, RESET
    }

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        val s = (context.context() as ScriptContext)
        if (s.getManager() == null) {
            throw RuntimeException("No manager selected.")
        }
        if (!s.entitySelected()) {
            throw RuntimeException("No entity selected.")
        }
        return context.newFrame(viewer).run<Any>().thenAccept { viewer ->
            s.getEntities()!!.filterNotNull().forEach {
                when (symbol) {
                    Symbol.ADD -> {
                        Bukkit.getPlayerExact(viewer.toString())?.run {
                            it.addViewer(this)
                        }
                    }
                    Symbol.REMOVE -> {
                        Bukkit.getPlayerExact(viewer.toString())?.run {
                            it.removeViewer(this)
                        }
                    }
                    Symbol.RESET -> it.clearViewer()
                }
            }
        }
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
                else -> throw ScriptHandler.loadError("Unknown viewer operator $type")
            }
            ActionViewer(symbol, if (symbol != Symbol.RESET) it.next(ArgTypes.ACTION) else null)
        }
    }
}